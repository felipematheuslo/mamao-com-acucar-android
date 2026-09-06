package com.felipelaurindo.mamaocomacucar.ui.map

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
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
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.formatDistance
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes
import androidx.compose.ui.draw.rotate
import android.view.MotionEvent
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import kotlin.math.abs
import kotlin.math.atan2

// Overlay de rotação com ativação por ângulo inicial (threshold) e rotação contínua e suave.
// Previne que o zoom em pinça gire o mapa acidentalmente, e garante 60fps sem engasgos ou saltos.
private class ThresholdRotationGestureOverlay(
    private val thresholdDegrees: Float = 8f,
    private val onOrientationChanged: ((Float) -> Unit)? = null
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
            return baseUrl +
                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
                    MapTileIndex.getY(pMapTileIndex) + "/" +
                    MapTileIndex.getX(pMapTileIndex)
        }
    }
}

// Fonte de mapa Topográfico / Relevo (Esri World Topo Map) com decodificação otimizada e RGB_565
private val esriTopoSource: OnlineTileSourceBase by lazy {
    object : OptimizedOnlineTileSource(
        "EsriTopo", 0, 19, 256, ".jpg",
        arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            return baseUrl +
                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
                    MapTileIndex.getY(pMapTileIndex) + "/" +
                    MapTileIndex.getX(pMapTileIndex)
        }
    }
}

private fun getTileSourceForStyle(style: String): org.osmdroid.tileprovider.tilesource.ITileSource = when (style) {
    "satellite" -> esriSatelliteSource
    else -> esriTopoSource
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    currentUser: LoggedUser,
    onLogout: () -> Unit,
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
    var pinCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var previewTree by remember { mutableStateOf<com.felipelaurindo.mamaocomacucar.data.model.TreeItem?>(null) }
    var pulsingTreeId by remember { mutableStateOf<String?>(null) }

    // Clear marker pulse effect after 2 seconds
    LaunchedEffect(pulsingTreeId) {
        if (pulsingTreeId != null) {
            kotlinx.coroutines.delay(2000L)
            pulsingTreeId = null
        }
    }

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    val isAnySheetOrDialogActive = isTreeListOpen || isTreeDetailOpen || isFruitCatalogOpen || isAddingTree || isAddDialogOpen || isAccountSettingsOpen || isAppSettingsOpen
    val isBannerVisible = !isAnySheetOrDialogActive && previewTree == null

    val bottomOffset by animateDpAsState(
        targetValue = when {
            isAddingTree -> 248.dp
            previewTree != null -> 230.dp
            else -> 144.dp
        },
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
                        }
                    )
                    overlays.add(rotationOverlay)

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

                // Tree markers
                val filteredTrees = mapViewModel.getFilteredTrees()
                for (tw in filteredTrees) {
                    val tree = tw.tree
                    val isPulsing = tree.id == pulsingTreeId
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(tree.latitude, tree.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = tree.species
                        snippet = tree.name
                        icon = createFruitVectorDrawable(context, tree.species, tree.currentStatus, isPulsing = isPulsing)
                        setOnMarkerClickListener { _, _ ->
                            previewTree = tree
                            pulsingTreeId = tree.id
                            mapViewModel.setMapCenter(tree.latitude, tree.longitude)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
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

        // ---- Floating Header ----
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(Stone200)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User info avatar & name
                Surface(
                    shape = CircleShape,
                    color = Stone100,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(badge.icon, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        currentUser.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Stone900,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        "@${currentUser.username}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                        color = MamaoOrange,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action buttons row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Account settings / Profile
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Stone100,
                        onClick = { isAccountSettingsOpen = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "Perfil",
                                tint = Stone700,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // App settings
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Stone100,
                        onClick = { isAppSettingsOpen = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = "Configurações",
                                tint = Stone700,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-16).dp)
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
                            .width(2.dp)
                            .height(14.dp)
                            .background(MamaoOrange)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MamaoOrange.copy(alpha = 0.7f))
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
                            Text("Confirmar Local 🌳", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // ---- Tree List Bottom Sheet ----
        if (isTreeListOpen) {
            TreeListSheet(
                mapViewModel = mapViewModel,
                creatorUsernames = creatorUsernames,
                onSelectTree = { tree ->
                    previewTree = tree
                    pulsingTreeId = tree.id
                    mapViewModel.setMapCenter(tree.latitude, tree.longitude)
                    isTreeListOpen = false
                    isTreeDetailOpen = false
                    // Limpa a busca para que todos os marcadores voltem ao mapa
                    mapViewModel.setSearchQuery("")
                    mapViewModel.setStatusFilter("todos")
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
        if (isTreeDetailOpen && selectedTree != null) {
            TreeDetailSheet(
                tree = selectedTree!!,
                updates = updates,
                currentUser = currentUser,
                creatorUsernames = creatorUsernames,
                mapViewModel = mapViewModel,
                onDismiss = {
                    isTreeDetailOpen = false
                    mapViewModel.selectTree(null)
                }
            )
        }

        // ---- Compass / Reset North FAB ----
        AnimatedVisibility(
            visible = kotlin.math.abs(mapOrientation) > 1f && !isTreeListOpen && !isTreeDetailOpen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 88.dp)
                .statusBarsPadding(),
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
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    Icons.Outlined.Navigation,
                    contentDescription = "Redefinir orientação para o Norte",
                    tint = Rose600,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(-mapOrientation)
                )
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

        // ---- Floating AdMob Banner (Entre a barra inferior e o botão de GPS) ----
        AnimatedVisibility(
            visible = isBannerVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .navigationBarsPadding(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(Stone200)
                )
            ) {
                AdMobBanner(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ---- Bottom Navigation ----
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explorar
                BottomNavItem(
                    icon = Icons.Outlined.Home,
                    label = "Explorar",
                    selected = !isTreeListOpen && !isAddingTree && !isFruitCatalogOpen,
                    onClick = {
                        isTreeListOpen = false
                        isFruitCatalogOpen = false
                        mapViewModel.setIsAddingTree(false)
                        pinCoordinates = null
                    }
                )
                // Buscar
                BottomNavItem(
                    icon = Icons.Outlined.Search,
                    label = "Buscar",
                    selected = isTreeListOpen,
                    onClick = {
                        isTreeListOpen = !isTreeListOpen
                        isFruitCatalogOpen = false
                        mapViewModel.setIsAddingTree(false)
                        pinCoordinates = null
                    }
                )
                // Catálogo
                BottomNavItem(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    label = "Catálogo",
                    selected = isFruitCatalogOpen,
                    onClick = {
                        isFruitCatalogOpen = !isFruitCatalogOpen
                        isTreeListOpen = false
                        mapViewModel.setIsAddingTree(false)
                        pinCoordinates = null
                    }
                )
                // Mapear
                BottomNavItem(
                    icon = Icons.Outlined.Add,
                    label = "Mapear",
                    selected = isAddingTree,
                    onClick = {
                        isTreeListOpen = false
                        isFruitCatalogOpen = false
                        val next = !isAddingTree
                        mapViewModel.setIsAddingTree(next)
                        if (next) {
                            mapViewModel.showToast("📍 Mova o mapa para alinhar a árvore com o marcador.")
                        } else {
                            pinCoordinates = null
                        }
                    }
                )
            }
        }

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
                onDismiss = { isAppSettingsOpen = false }
            )
        }

        // ---- Fruit Catalog Sheet ----
        if (isFruitCatalogOpen) {
            FruitCatalogSheet(
                onSearchFruitOnMap = { fruitName ->
                    mapViewModel.setStatusFilter("todos")
                    mapViewModel.setSearchQuery(fruitName)
                    isFruitCatalogOpen = false
                    isTreeListOpen = true
                },
                onDismiss = { isFruitCatalogOpen = false }
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
                .padding(top = 80.dp)
                .statusBarsPadding()
        ) {
            ToastOverlay(message = toastMessage)
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MamaoOrangeLight else Color.Transparent,
        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) MamaoOrange else Stone400,
                modifier = Modifier.size(22.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold
                ),
                color = if (selected) MamaoOrange else Stone500
            )
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
    isPulsing: Boolean = false
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizeDp = if (isPulsing) 52 else 40
    val sizePx = (sizeDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = sizePx * (if (isPulsing) 0.36f else 0.44f)

    // Background color based on status
    val bgColor = when (status) {
        TreeStatus.PRONTO -> android.graphics.Color.parseColor("#FEF2F2")
        TreeStatus.CRESCENDO -> android.graphics.Color.parseColor("#F0FDF4")
        TreeStatus.FLORINDO -> android.graphics.Color.parseColor("#FDF2F8")
        TreeStatus.VAZIO -> android.graphics.Color.parseColor("#F5F5F4")
    }

    val strokeColor = when (status) {
        TreeStatus.PRONTO -> android.graphics.Color.parseColor("#EF4444")
        TreeStatus.CRESCENDO -> android.graphics.Color.parseColor("#22C55E")
        TreeStatus.FLORINDO -> android.graphics.Color.parseColor("#EC4899")
        TreeStatus.VAZIO -> android.graphics.Color.parseColor("#78716C")
    }

    // Glowing pulsing outer ring
    if (isPulsing) {
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
        color = if (isPulsing) android.graphics.Color.parseColor("#F97316") else strokeColor
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
        vectorDrawable.draw(canvas)
    }

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
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
