package com.felipelaurindo.mamaocomacucar.ui.map

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.components.AdMobBanner
import com.felipelaurindo.mamaocomacucar.ui.map.components.*
import com.felipelaurindo.mamaocomacucar.ui.settings.AccountSettingsSheet
import com.felipelaurindo.mamaocomacucar.ui.settings.AppSettingsSheet
import com.felipelaurindo.mamaocomacucar.ui.auth.components.AuthPromptSheet
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.calculateDistance
import com.felipelaurindo.mamaocomacucar.util.formatDistance
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes
import androidx.compose.ui.draw.rotate
import android.view.MotionEvent
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import com.felipelaurindo.mamaocomacucar.data.model.TreeItem
import kotlin.math.abs
import kotlin.math.atan2

// Overlay de rotação com ativação por ângulo inicial (threshold) e rotação contínua e suave.
// Previne que o zoom em pinça gire o mapa acidentalmente, e garante 60fps sem engasgos ou saltos.
private class ThresholdRotationGestureOverlay(
    private val thresholdDegrees: Float = 8f,
    private val onOrientationChanged: ((Float) -> Unit)? = null,
    private val onGestureEnd: ((Double) -> Unit)? = null
) : Overlay() {
    private var initialFingerAngle: Float = 0f
    private var rotationPivotAngle: Float = 0f
    private var rotationPivotOrientation: Float = 0f
    private var isRotating: Boolean = false
    private var pointerId1: Int = MotionEvent.INVALID_POINTER_ID
    private var pointerId2: Int = MotionEvent.INVALID_POINTER_ID

    override fun onTouchEvent(event: MotionEvent, mapView: MapView): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    pointerId1 = event.getPointerId(0)
                    pointerId2 = event.getPointerId(1)
                    val idx1 = event.findPointerIndex(pointerId1)
                    val idx2 = event.findPointerIndex(pointerId2)
                    if (idx1 != -1 && idx2 != -1) {
                        initialFingerAngle = calculateAngle(event, idx1, idx2)
                        isRotating = false
                    }
                } else {
                    isRotating = false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2 && pointerId1 != MotionEvent.INVALID_POINTER_ID && pointerId2 != MotionEvent.INVALID_POINTER_ID) {
                    val idx1 = event.findPointerIndex(pointerId1)
                    val idx2 = event.findPointerIndex(pointerId2)

                    if (idx1 != -1 && idx2 != -1) {
                        val currentAngle = calculateAngle(event, idx1, idx2)

                        if (!isRotating) {
                            val diff = normalizeDelta(currentAngle - initialFingerAngle)
                            // Verifica se o usuário iniciou um movimento intencional de rotação
                            if (abs(diff) >= thresholdDegrees) {
                                isRotating = true
                                rotationPivotAngle = currentAngle
                                rotationPivotOrientation = mapView.mapOrientation
                            }
                        }

                        // Após iniciar a rotação, desabilita a verificação e acompanha os dedos com precisão contínua
                        if (isRotating) {
                            val deltaFromPivot = normalizeDelta(currentAngle - rotationPivotAngle)
                            var newOrientation = (rotationPivotOrientation + deltaFromPivot + 360f) % 360f

                            // Snapping suave para o Norte se estiver a menos de 2°
                            if (newOrientation < 2f || newOrientation > 358f) {
                                newOrientation = 0f
                            }
                            mapView.mapOrientation = newOrientation
                        }
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isRotating) {
                    onOrientationChanged?.invoke(mapView.mapOrientation)
                }
                isRotating = false
                pointerId1 = MotionEvent.INVALID_POINTER_ID
                pointerId2 = MotionEvent.INVALID_POINTER_ID
                onGestureEnd?.invoke(mapView.zoomLevelDouble)
            }
        }
        return false // Permite que o zoom nativo em pinça continue sem interferência
    }

    private fun normalizeDelta(angle: Float): Float {
        var delta = angle % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        return delta
    }

    private fun calculateAngle(event: MotionEvent, idx1: Int, idx2: Int): Float {
        val xDiff = event.getX(idx2) - event.getX(idx1)
        val yDiff = event.getY(idx2) - event.getY(idx1)
        return Math.toDegrees(atan2(yDiff.toDouble(), xDiff.toDouble())).toFloat()
    }
}

// Fonte de mapa de Satélite (Esri World Imagery) com decodificação otimizada e RGB_565
private val esriSatelliteSource: OnlineTileSourceBase by lazy {
    object : OptimizedOnlineTileSource(
        "EsriSatellite", 0, 18, 256, ".jpg",
        arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "$baseUrl$zoom/$y/$x$mImageFilenameEnding"
        }
    }
}

// Fonte de mapa de Relevo (Esri World Topo Map) livre e sem marcas d'água
private val esriTopoSource: OnlineTileSourceBase by lazy {
    object : OptimizedOnlineTileSource(
        "EsriTopo", 0, 19, 256, ".jpg",
        arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "$baseUrl$zoom/$y/$x$mImageFilenameEnding"
        }
    }
}

private fun getTileSourceForStyle(style: String): OnlineTileSourceBase {
    return when (style) {
        "satellite" -> esriSatelliteSource
        else -> esriTopoSource
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    currentUser: LoggedUser,
    onLogout: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    isGoogleLoading: Boolean = false,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val trees by mapViewModel.trees.collectAsState()
    val selectedTree by mapViewModel.selectedTree.collectAsState()
    val updates by mapViewModel.updates.collectAsState()
    val userLocation by mapViewModel.userLocation.collectAsState()
    val mapCenter by mapViewModel.mapCenter.collectAsState()
    val toastMessage by mapViewModel.toastMessage.collectAsState()
    val isAddingTree by mapViewModel.isAddingTree.collectAsState()
    val creatorUsernames by mapViewModel.creatorUsernames.collectAsState()
    val searchQuery by mapViewModel.searchQuery.collectAsState()

    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val savedStyle = prefs.getString("map_style", "topo")
    var mapStyle by remember { mutableStateOf(if (savedStyle == "satellite") "satellite" else "topo") }
    var mapOrientation by remember { mutableFloatStateOf(0f) }
    var isTreeDetailOpen by remember { mutableStateOf(false) }
    var isTreeListOpen by remember { mutableStateOf(false) }
    var isFruitCatalogOpen by remember { mutableStateOf(false) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var isAccountSettingsOpen by remember { mutableStateOf(false) }
    var isAppSettingsOpen by remember { mutableStateOf(false) }
    var isAuthPromptOpen by remember { mutableStateOf(false) }
    var authPromptSubtitle by remember { mutableStateOf<String?>(null) }
    var pinCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var previewTree by remember { mutableStateOf<TreeItem?>(null) }
    var pulsingTreeId by remember { mutableStateOf<String?>(null) }
    var currentMapZoom by remember { mutableDoubleStateOf(15.0) }

    // Close auth prompt if user logs in
    LaunchedEffect(currentUser.isGuest) {
        if (!currentUser.isGuest) {
            isAuthPromptOpen = false
        }
    }

    // Clear marker pulse effect after 2 seconds
    LaunchedEffect(pulsingTreeId) {
        if (pulsingTreeId != null) {
            kotlinx.coroutines.delay(2000L)
            pulsingTreeId = null
        }
    }

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    val isAnySheetOrDialogActive = isTreeListOpen || isTreeDetailOpen || isFruitCatalogOpen || isAddingTree || isAddDialogOpen || isAccountSettingsOpen || isAppSettingsOpen || isAuthPromptOpen
    val isBannerVisible = !isAnySheetOrDialogActive && previewTree == null

    val bottomOffset by animateDpAsState(
        targetValue = when {
            isAddingTree -> 248.dp
            previewTree != null -> 232.dp
            isBannerVisible -> 140.dp
            else -> 80.dp
        },
        animationSpec = androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "bottomOffset"
    )

    // Gamification
    val userTreeCount = mapViewModel.getUserMappedTreesCount(currentUser.uid)
    val badge = mapViewModel.getUserBadge(userTreeCount)

    // Location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            requestCurrentLocation(context, mapViewModel) { lat, lng ->
                mapViewModel.setMapCenter(lat, lng)
            }
        }
    }

    // Request location on first launch
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Update map center when it changes
    LaunchedEffect(mapCenter) {
        mapViewRef.value?.controller?.animateTo(GeoPoint(mapCenter.first, mapCenter.second), 17.5, 1000L)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ---- OSMDroid Map ----
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
                .navigationBarsPadding(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(mapCenter.first, mapCenter.second))
                    setTileSource(getTileSourceForStyle(mapStyle))

                    // Overlay de rotação persistente com ativação por ângulo e rastreamento contínuo
                    val rotationOverlay = ThresholdRotationGestureOverlay(
                        thresholdDegrees = 8f,
                        onOrientationChanged = { newOrientation ->
                            mapOrientation = newOrientation
                        },
                        onGestureEnd = { finalZoom ->
                            if (abs(finalZoom - currentMapZoom) >= 0.05) {
                                currentMapZoom = finalZoom
                            }
                        }
                    )
                    overlays.add(rotationOverlay)

                    addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            if (isAddingTree) {
                                val center = this@apply.mapCenter
                                pinCoordinates = Pair(center.latitude, center.longitude)
                            }
                            return false
                        }
                        override fun onZoom(event: ZoomEvent?): Boolean {
                            val newZoom = zoomLevelDouble
                            if (abs(newZoom - currentMapZoom) >= 0.3) {
                                currentMapZoom = newZoom
                            }
                            return false
                        }
                    })

                    mapViewRef.value = this
                }
            },
            update = { mapView ->
                // Remove apenas os marcadores antigos sem destruir os overlays persistentes
                mapView.overlays.removeAll { it is Marker }

                // User location marker
                val userMarker = Marker(mapView).apply {
                    position = GeoPoint(userLocation.first, userLocation.second)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Você está aqui! 📍"
                    icon = createCircleDrawable(context, android.graphics.Color.parseColor("#2563EB"), 12)
                }
                mapView.overlays.add(userMarker)

                // Marcadores de árvores agrupados (clusters) ou individuais
                val filteredTrees = mapViewModel.getFilteredTrees()
                val density = context.resources.displayMetrics.density
                val clusterRadiusPx = 44f * density
                val clusters = clusterTrees(
                    items = filteredTrees,
                    zoom = currentMapZoom,
                    clusterRadiusPx = clusterRadiusPx,
                    pulsingTreeId = pulsingTreeId
                )

                for (cluster in clusters) {
                    if (cluster.trees.size == 1) {
                        val tree = cluster.trees.first()
                        val isPulsing = tree.id == pulsingTreeId
                        val distanceKm = calculateDistance(userLocation.first, userLocation.second, tree.latitude, tree.longitude)
                        val isObfuscated = currentUser.isGuest && distanceKm > 2.0

                        val marker = Marker(mapView).apply {
                            position = GeoPoint(tree.latitude, tree.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = if (isObfuscated) "Fruteira fora do raio de 2 km 🔒" else tree.species
                            snippet = if (isObfuscated) "Cadastre-se para ver os detalhes" else tree.name
                            if (isObfuscated) {
                                alpha = 0.40f
                            }
                            icon = createFruitVectorDrawable(
                                context = context,
                                species = tree.species,
                                status = tree.currentStatus,
                                isPulsing = isPulsing,
                                isObfuscated = isObfuscated
                            )
                            setOnMarkerClickListener { _, _ ->
                                if (isObfuscated) {
                                    val distFormatted = formatDistance(distanceKm)
                                    authPromptSubtitle = "Esta fruteira está a $distFormatted de você, fora do raio de 2 km do modo visitante. Conecte sua conta gratuita para explorar árvores em qualquer cidade!"
                                    isAuthPromptOpen = true
                                } else {
                                    previewTree = tree
                                    pulsingTreeId = tree.id
                                    mapViewModel.setMapCenter(tree.latitude, tree.longitude)
                                }
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    } else {
                        val allTreesObfuscated = currentUser.isGuest && cluster.trees.all {
                            calculateDistance(userLocation.first, userLocation.second, it.latitude, it.longitude) > 2.0
                        }

                        val marker = Marker(mapView).apply {
                            position = GeoPoint(cluster.centerLat, cluster.centerLng)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = "${cluster.trees.size} fruteiras"
                            snippet = if (allTreesObfuscated) "Fora do raio de 2 km 🔒" else "Toque para aproximar"
                            if (allTreesObfuscated) {
                                alpha = 0.45f
                            }
                            icon = createClusterVectorDrawable(
                                context = context,
                                count = cluster.trees.size,
                                isPulsing = cluster.isPulsing,
                                isObfuscated = allTreesObfuscated
                            )
                            setOnMarkerClickListener { _, _ ->
                                val currentZoom = mapView.zoomLevelDouble
                                if (currentZoom < 18.0) {
                                    mapView.controller.animateTo(
                                        GeoPoint(cluster.centerLat, cluster.centerLng),
                                        (currentZoom + 2.0).coerceAtMost(19.0),
                                        500L
                                    )
                                } else {
                                    val firstTree = cluster.trees.first()
                                    val distKm = calculateDistance(userLocation.first, userLocation.second, firstTree.latitude, firstTree.longitude)
                                    if (currentUser.isGuest && distKm > 2.0) {
                                        val distFormatted = formatDistance(distKm)
                                        authPromptSubtitle = "Estas fruteiras estão a $distFormatted de você, fora do raio de 2 km do modo visitante. Conecte sua conta gratuita para explorar árvores em qualquer cidade!"
                                        isAuthPromptOpen = true
                                    } else {
                                        previewTree = firstTree
                                        pulsingTreeId = firstTree.id
                                        mapViewModel.setMapCenter(cluster.centerLat, cluster.centerLng)
                                    }
                                }
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                }

                // Update tile source based on style
                val tileSource = getTileSourceForStyle(mapStyle)
                if (mapView.tileProvider.tileSource.name() != tileSource.name()) {
                    mapView.setTileSource(tileSource)
                }

                // Track center for pin placement mode
                if (isAddingTree) {
                    val center = mapView.mapCenter
                    pinCoordinates = Pair(center.latitude, center.longitude)
                }

                mapView.invalidate()
            }
        )

        // ---- Floating Search Bar ----
        FloatingSearchBar(
            query = searchQuery,
            onQueryChange = { query -> mapViewModel.setSearchQuery(query) },
            onClearQuery = { mapViewModel.setSearchQuery("") },
            onProfileClick = {
                if (currentUser.isGuest) {
                    authPromptSubtitle = "Você está navegando como visitante. Conecte sua conta para salvar seu progresso!"
                    isAuthPromptOpen = true
                } else {
                    isAccountSettingsOpen = true
                }
            },
            isGuest = currentUser.isGuest,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .statusBarsPadding()
        )

        // ---- Pin overlay for adding tree ----
        if (isAddingTree && pinCoordinates != null) {
            // Center pin
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                // Mira no solo ancorada exatamente no centro geográfico do mapa (coordenada salva)
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MamaoOrange.copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, MamaoOrange.copy(alpha = 0.75f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MamaoOrange, CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }

                // Pino (balão de cereja + haste) posicionado diretamente acima do pontinho
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-35).dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            width = 3.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(MamaoOrange)
                        ),
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🍒", fontSize = 20.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(2.5.dp)
                            .height(18.dp)
                            .background(MamaoOrange)
                    )
                }
            }

            // Bottom card with coordinates
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 72.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("📍", fontSize = 20.sp)
                        Column {
                            Text(
                                "Posicionar a Fruteira",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = Stone950
                            )
                            Text(
                                "Arraste o mapa para centralizar a árvore exatamente sobre o marcador e confirme o local.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MamaoOrange
                            )
                            Text(
                                "COORDENADAS: ${String.format("%.5f", pinCoordinates!!.first)}, ${String.format("%.5f", pinCoordinates!!.second)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                ),
                                color = Stone500,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                mapViewModel.setIsAddingTree(false)
                                pinCoordinates = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = {
                                // Update pin to current map center
                                mapViewRef.value?.let { mv ->
                                    val center = mv.mapCenter
                                    pinCoordinates = Pair(center.latitude, center.longitude)
                                }
                                isAddDialogOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                        ) {
                            Text("Confirmar Local", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // ---- Tree List Bottom Sheet ----
        AnimatedVisibility(
            visible = isTreeListOpen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(180, easing = androidx.compose.animation.core.FastOutLinearInEasing)
            ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(120))
        ) {
            TreeListSheet(
                mapViewModel = mapViewModel,
                creatorUsernames = creatorUsernames,
                isGuest = currentUser.isGuest,
                onRequestAuth = { msg ->
                    authPromptSubtitle = msg
                    isAuthPromptOpen = true
                },
                onSelectTree = { tree ->
                    val distKm = calculateDistance(userLocation.first, userLocation.second, tree.latitude, tree.longitude)
                    if (currentUser.isGuest && distKm > 2.0) {
                        val distFormatted = formatDistance(distKm)
                        authPromptSubtitle = "Esta fruteira está a $distFormatted de você, fora do raio de 2 km do modo visitante. Conecte sua conta gratuita para explorar árvores em qualquer lugar!"
                        isAuthPromptOpen = true
                    } else {
                        previewTree = tree
                        pulsingTreeId = tree.id
                        mapViewModel.setMapCenter(tree.latitude, tree.longitude)
                        isTreeListOpen = false
                        isTreeDetailOpen = false
                        // Limpa a busca para que todos os marcadores voltem ao mapa
                        mapViewModel.setSearchQuery("")
                        mapViewModel.setStatusFilter("todos")
                    }
                },
                onDismiss = {
                    isTreeListOpen = false
                    // Limpa a busca para que todos os marcadores voltem ao mapa
                    mapViewModel.setSearchQuery("")
                    mapViewModel.setStatusFilter("todos")
                }
            )
        }

        // ---- Tree Detail Sheet ----
        AnimatedVisibility(
            visible = isTreeDetailOpen && selectedTree != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(180, easing = androidx.compose.animation.core.FastOutLinearInEasing)
            ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(120))
        ) {
            TreeDetailSheet(
                tree = selectedTree!!,
                updates = updates,
                currentUser = currentUser,
                creatorUsernames = creatorUsernames,
                mapViewModel = mapViewModel,
                onRequestAuth = { msg ->
                    authPromptSubtitle = msg
                    isAuthPromptOpen = true
                },
                onDismiss = {
                    isTreeDetailOpen = false
                    mapViewModel.selectTree(null)
                }
            )
        }

        // ---- Fruit Catalog Sheet ----
        AnimatedVisibility(
            visible = isFruitCatalogOpen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(180, easing = androidx.compose.animation.core.FastOutLinearInEasing)
            ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(120))
        ) {
            FruitCatalogSheet(
                isGuest = currentUser.isGuest,
                onRequestAuth = { msg ->
                    authPromptSubtitle = msg
                    isAuthPromptOpen = true
                },
                onSearchFruitOnMap = { fruitName ->
                    mapViewModel.setStatusFilter("todos")
                    mapViewModel.setSearchQuery(fruitName)
                    isFruitCatalogOpen = false
                },
                onDismiss = { isFruitCatalogOpen = false }
            )
        }

        // ---- Top Right Controls (Camadas e Bússola) ----
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 76.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão de Camadas do Mapa (Satélite / Relevo)
            AnimatedVisibility(
                visible = !isTreeListOpen && !isTreeDetailOpen,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    onClick = { isAppSettingsOpen = true },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Stone200),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Layers,
                            contentDescription = "Estilo do mapa",
                            tint = Stone700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bússola (apenas quando mapa estiver rotacionado)
            AnimatedVisibility(
                visible = kotlin.math.abs(mapOrientation) > 1f && !isTreeListOpen && !isTreeDetailOpen,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        mapViewRef.value?.let { mv ->
                            mv.mapOrientation = 0f
                            mapOrientation = 0f
                        }
                    },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = Stone900,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        Icons.Outlined.Navigation,
                        contentDescription = "Redefinir orientação para o Norte",
                        tint = Rose600,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(-mapOrientation)
                    )
                }
            }
        }

        // ---- GPS FAB ----
        AnimatedVisibility(
            visible = !isTreeListOpen && !isTreeDetailOpen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = bottomOffset)
                .navigationBarsPadding(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            FloatingActionButton(
                onClick = {
                    requestCurrentLocation(context, mapViewModel) { lat, lng ->
                        mapViewRef.value?.controller?.apply {
                            animateTo(GeoPoint(lat, lng), 18.0, 1000L)
                        }
                    }
                },
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = Stone900
            ) {
                Icon(Icons.Outlined.MyLocation, contentDescription = "Minha localização")
            }
        }

        // ---- Bottom Dock (AdMob + Navigation) ----
        MapBottomBar(
            isExploring = isTreeListOpen,
            isAddingTree = isAddingTree,
            isFruitCatalogOpen = isFruitCatalogOpen,
            onExploreClick = {
                isFruitCatalogOpen = false
                mapViewModel.setIsAddingTree(false)
                pinCoordinates = null
                isTreeListOpen = !isTreeListOpen
            },
            onToggleAddTreeClick = {
                if (currentUser.isGuest) {
                    authPromptSubtitle = "Cadastre-se gratuitamente para marcar árvores frutíferas no mapa, registrar fotos e conquistar selos de cultivador."
                    isAuthPromptOpen = true
                } else {
                    isTreeListOpen = false
                    isFruitCatalogOpen = false
                    val next = !isAddingTree
                    mapViewModel.setIsAddingTree(next)
                    if (!next) {
                        pinCoordinates = null
                    }
                }
            },
            onToggleFruitCatalogClick = {
                isTreeListOpen = false
                isFruitCatalogOpen = !isFruitCatalogOpen
                mapViewModel.setIsAddingTree(false)
                pinCoordinates = null
            },
            isBannerVisible = isBannerVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            adBannerContent = {
                AdMobBanner(modifier = Modifier.fillMaxWidth())
            }
        )

        // ---- Add Tree Dialog ----
        if (isAddDialogOpen && pinCoordinates != null) {
            AddTreeDialog(
                coordinates = pinCoordinates!!,
                currentUser = currentUser,
                mapViewModel = mapViewModel,
                onDismiss = {
                    isAddDialogOpen = false
                    mapViewModel.setIsAddingTree(false)
                    pinCoordinates = null
                }
            )
        }

        // ---- Account Settings ----
        if (isAccountSettingsOpen) {
            AccountSettingsSheet(
                currentUser = currentUser,
                userTreeCount = userTreeCount,
                badge = badge,
                onLogout = onLogout,
                onDismiss = { isAccountSettingsOpen = false }
            )
        }

        // ---- App Settings ----
        if (isAppSettingsOpen) {
            AppSettingsSheet(
                currentMapStyle = mapStyle,
                onMapStyleChange = { newStyle ->
                    mapStyle = newStyle
                    prefs.edit().putString("map_style", newStyle).apply()
                },
                onShowToast = { mapViewModel.showToast(it) },
                isGuest = currentUser.isGuest,
                onRequestAuth = { msg ->
                    authPromptSubtitle = msg
                    isAuthPromptOpen = true
                },
                onDismiss = { isAppSettingsOpen = false }
            )
        }

        // ---- Auth Prompt Sheet (Guest Mode Soft-Gate) ----
        if (isAuthPromptOpen) {
            AuthPromptSheet(
                title = "Faça parte da nossa colheita!",
                subtitle = authPromptSubtitle ?: "Cadastre-se gratuitamente para marcar árvores frutíferas no mapa, registrar fotos e conquistar selos de cultivador.",
                onGoogleSignIn = onGoogleSignIn,
                isGoogleLoading = isGoogleLoading,
                onEmailAction = {
                    isAuthPromptOpen = false
                    onNavigateToLogin()
                },
                onDismiss = { isAuthPromptOpen = false }
            )
        }

        // ---- Tree Quick Preview Card ----
        if (previewTree != null && !isAnySheetOrDialogActive) {
            val previewCreatorUsername = mapViewModel.getCreatorUsername(previewTree!!.createdBy, previewTree!!.createdByName)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
                    .navigationBarsPadding()
            ) {
                TreeQuickPreviewCard(
                    tree = previewTree!!,
                    userLocation = userLocation,
                    creatorUsername = previewCreatorUsername,
                    onOpenFullDetails = {
                        mapViewModel.selectTree(previewTree)
                        isTreeDetailOpen = true
                        previewTree = null
                    },
                    onClose = {
                        previewTree = null
                        pulsingTreeId = null
                    }
                )
            }
        }

        // ---- Toast ----
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp)
                .statusBarsPadding()
        ) {
            ToastOverlay(message = toastMessage)
        }
    }
}

// Helper to create a simple circle drawable for the user location marker
private fun createCircleDrawable(context: Context, color: Int, radiusDp: Int): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val radiusPx = (radiusDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(radiusPx * 2, radiusPx * 2, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Pulse ring
    val pulsePaint = android.graphics.Paint().apply {
        this.color = android.graphics.Color.argb(60, 37, 99, 235)
        isAntiAlias = true
    }
    canvas.drawCircle(radiusPx.toFloat(), radiusPx.toFloat(), radiusPx.toFloat(), pulsePaint)

    // Core dot
    val paint = android.graphics.Paint().apply {
        this.color = color
        isAntiAlias = true
    }
    canvas.drawCircle(radiusPx.toFloat(), radiusPx.toFloat(), radiusPx * 0.5f, paint)

    // White border
    val borderPaint = android.graphics.Paint().apply {
        this.color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3f * density
        isAntiAlias = true
    }
    canvas.drawCircle(radiusPx.toFloat(), radiusPx.toFloat(), radiusPx * 0.5f, borderPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Helper to create a fruit VectorDrawable map marker
private fun createFruitVectorDrawable(
    context: Context,
    species: String,
    status: TreeStatus,
    isPulsing: Boolean = false,
    isObfuscated: Boolean = false
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizeDp = if (isPulsing) 52 else 40
    val sizePx = (sizeDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = sizePx * (if (isPulsing) 0.36f else 0.44f)

    // Background color based on status or obfuscation
    val bgColor = if (isObfuscated) {
        android.graphics.Color.parseColor("#F5F5F4") // Stone100
    } else {
        when (status) {
            TreeStatus.PRONTO -> android.graphics.Color.parseColor("#FEF2F2")
            TreeStatus.CRESCENDO -> android.graphics.Color.parseColor("#F0FDF4")
            TreeStatus.FLORINDO -> android.graphics.Color.parseColor("#FDF2F8")
            TreeStatus.VAZIO -> android.graphics.Color.parseColor("#F5F5F4")
        }
    }

    val strokeColor = if (isObfuscated) {
        android.graphics.Color.parseColor("#A8A29E") // Stone400
    } else {
        when (status) {
            TreeStatus.PRONTO -> android.graphics.Color.parseColor("#EF4444")
            TreeStatus.CRESCENDO -> android.graphics.Color.parseColor("#22C55E")
            TreeStatus.FLORINDO -> android.graphics.Color.parseColor("#EC4899")
            TreeStatus.VAZIO -> android.graphics.Color.parseColor("#78716C")
        }
    }

    // Glowing pulsing outer ring
    if (isPulsing && !isObfuscated) {
        val haloPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(120, 249, 115, 22) // MamaoOrange glowing halo
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, sizePx * 0.48f, haloPaint)
    }

    // Background Circle
    val bgPaint = android.graphics.Paint().apply {
        color = bgColor
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, bgPaint)

    // Border
    val borderPaint = android.graphics.Paint().apply {
        color = if (isPulsing && !isObfuscated) android.graphics.Color.parseColor("#F97316") else strokeColor
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = (if (isPulsing) 3.5f else 2.5f) * density
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, borderPaint)

    // VectorDrawable Icon
    val drawableRes = getFruitDrawableRes(species)
    val vectorDrawable = ContextCompat.getDrawable(context, drawableRes)
    if (vectorDrawable != null) {
        val iconSize = ((if (isPulsing) 26 else 24) * density).toInt()
        val left = (centerX - iconSize / 2f).toInt()
        val top = (centerY - iconSize / 2f).toInt()
        vectorDrawable.setBounds(left, top, left + iconSize, top + iconSize)
        if (isObfuscated) {
            vectorDrawable.alpha = 110 // Translúcido desbotado
        }
        vectorDrawable.draw(canvas)
    }

    // Badge de cadeado vetorial para itens ofuscados
    if (isObfuscated) {
        val lockBadgeRadius = 6.5f * density
        val lockBadgeX = centerX + radius * 0.65f
        val lockBadgeY = centerY + radius * 0.65f

        val lockBgPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#57534E") // Stone600
            isAntiAlias = true
        }
        canvas.drawCircle(lockBadgeX, lockBadgeY, lockBadgeRadius, lockBgPaint)

        val lockPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
        }
        val lockWidth = 4.5f * density
        val lockHeight = 3.5f * density
        val rectLeft = lockBadgeX - lockWidth / 2f
        val rectTop = lockBadgeY - lockHeight / 4f
        canvas.drawRoundRect(
            rectLeft,
            rectTop,
            rectLeft + lockWidth,
            rectTop + lockHeight,
            1f * density,
            1f * density,
            lockPaint
        )

        val shacklePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 1f * density
            isAntiAlias = true
        }
        val shackleRadius = 1.6f * density
        val shackleOval = android.graphics.RectF(
            lockBadgeX - shackleRadius,
            rectTop - shackleRadius * 1.6f,
            lockBadgeX + shackleRadius,
            rectTop + shackleRadius * 0.2f
        )
        canvas.drawArc(shackleOval, 180f, 180f, false, shacklePaint)
    }

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Helper to create a cluster VectorDrawable map marker showing the count of grouped trees
private fun createClusterVectorDrawable(
    context: Context,
    count: Int,
    isPulsing: Boolean = false,
    isObfuscated: Boolean = false
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizeDp = if (isPulsing) 52 else 42
    val sizePx = (sizeDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = sizePx * (if (isPulsing) 0.36f else 0.44f)

    // Halo pulsante quando a árvore selecionada está dentro deste agrupamento
    if (isPulsing && !isObfuscated) {
        val haloPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(120, 249, 115, 22) // MamaoOrange glowing halo
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, sizePx * 0.48f, haloPaint)
    }

    // Sombra sutil externa para profundidade
    val shadowPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(if (isObfuscated) 15 else 35, 0, 0, 0)
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY + 1.2f * density, radius, shadowPaint)

    // Fundo circular (MamaoOrangeLight ou Stone100 quando ofuscado)
    val bgPaint = android.graphics.Paint().apply {
        color = if (isObfuscated) android.graphics.Color.parseColor("#F5F5F4") else android.graphics.Color.parseColor("#FFF7ED")
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, bgPaint)

    // Borda (MamaoOrange ou Stone400 quando ofuscado)
    val borderPaint = android.graphics.Paint().apply {
        color = if (isObfuscated) android.graphics.Color.parseColor("#A8A29E") else android.graphics.Color.parseColor("#F97316")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = (if (isPulsing) 3.5f else 2.6f) * density
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, borderPaint)

    // Texto com a quantidade condensada ("3", "4", "6", etc.)
    val countText = if (count > 99) "99+" else count.toString()
    val textPaint = android.graphics.Paint().apply {
        color = if (isObfuscated) android.graphics.Color.parseColor("#78716C") else android.graphics.Color.parseColor("#C2410C")
        isAntiAlias = true
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = (if (countText.length > 2) 13f else 15f) * density
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }

    val fontMetrics = textPaint.fontMetrics
    val textY = centerY - (fontMetrics.ascent + fontMetrics.descent) / 2f
    canvas.drawText(countText, centerX, textY, textPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Representação de um agrupamento de árvores no mapa
private data class TreeCluster(
    val trees: List<TreeItem>,
    val centerLat: Double,
    val centerLng: Double,
    val isPulsing: Boolean
)

// Projeção Web Mercator (EPSG:3857) de coordenadas para pixels globais no zoom determinado
private fun projectToMercatorPixels(lat: Double, lng: Double, zoom: Double): Pair<Double, Double> {
    val x = (lng + 180.0) / 360.0 * 256.0 * Math.pow(2.0, zoom)
    val sinLat = Math.sin(Math.toRadians(lat)).coerceIn(-0.9999, 0.9999)
    val y = (0.5 - Math.log((1.0 + sinLat) / (1.0 - sinLat)) / (4.0 * Math.PI)) * 256.0 * Math.pow(2.0, zoom)
    return Pair(x, y)
}

// Algoritmo de agrupamento por raio euclidiano em pixels de tela
private fun clusterTrees(
    items: List<TreeWithDistance>,
    zoom: Double,
    clusterRadiusPx: Float,
    pulsingTreeId: String?
): List<TreeCluster> {
    if (items.isEmpty()) return emptyList()

    data class ProjectedItem(
        val tree: TreeItem,
        val x: Double,
        val y: Double
    )

    val projected = items.map { tw ->
        val (px, py) = projectToMercatorPixels(tw.tree.latitude, tw.tree.longitude, zoom)
        ProjectedItem(tw.tree, px, py)
    }

    val visited = BooleanArray(projected.size)
    val clusters = mutableListOf<TreeCluster>()
    val radiusSq = (clusterRadiusPx * clusterRadiusPx).toDouble()

    for (i in projected.indices) {
        if (visited[i]) continue
        visited[i] = true

        val root = projected[i]
        val clusterItems = mutableListOf(root.tree)
        var sumLat = root.tree.latitude
        var sumLng = root.tree.longitude
        var hasPulsing = (root.tree.id == pulsingTreeId)

        for (j in (i + 1) until projected.size) {
            if (visited[j]) continue
            val candidate = projected[j]
            val dx = root.x - candidate.x
            val dy = root.y - candidate.y
            val distSq = dx * dx + dy * dy

            if (distSq <= radiusSq) {
                visited[j] = true
                clusterItems.add(candidate.tree)
                sumLat += candidate.tree.latitude
                sumLng += candidate.tree.longitude
                if (candidate.tree.id == pulsingTreeId) {
                    hasPulsing = true
                }
            }
        }

        clusters.add(
            TreeCluster(
                trees = clusterItems,
                centerLat = sumLat / clusterItems.size,
                centerLng = sumLng / clusterItems.size,
                isPulsing = hasPulsing
            )
        )
    }

    return clusters
}

private fun requestCurrentLocation(
    context: Context,
    mapViewModel: MapViewModel,
    onLocationFound: ((Double, Double) -> Unit)? = null
) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (lastKnown != null) {
            mapViewModel.setUserLocation(lastKnown.latitude, lastKnown.longitude)
            onLocationFound?.invoke(lastKnown.latitude, lastKnown.longitude)
        }

        locationManager.requestSingleUpdate(
            LocationManager.GPS_PROVIDER,
            { location ->
                mapViewModel.setUserLocation(location.latitude, location.longitude)
                if (lastKnown == null) {
                    onLocationFound?.invoke(location.latitude, location.longitude)
                }
            },
            null
        )
    } catch (e: Exception) {
        android.util.Log.e("MapScreen", "Error requesting location", e)
    }
}
