package com.felipelaurindo.mamaocomacucar.ui.map

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.map.components.*
import com.felipelaurindo.mamaocomacucar.ui.settings.AccountSettingsSheet
import com.felipelaurindo.mamaocomacucar.ui.settings.AppSettingsSheet
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.formatDistance
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Map tile sources matching the web app's styles
private fun getCartoVoyager() = XYTileSource(
    "CartoVoyager", 0, 19, 256, ".png",
    arrayOf("https://a.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://c.basemaps.cartocdn.com/rastertiles/voyager/")
)

private fun getCartoDark() = XYTileSource(
    "CartoDark", 0, 19, 256, ".png",
    arrayOf("https://a.basemaps.cartocdn.com/dark_all/",
            "https://b.basemaps.cartocdn.com/dark_all/",
            "https://c.basemaps.cartocdn.com/dark_all/")
)

private fun getCartoPositron() = XYTileSource(
    "CartoPositron", 0, 19, 256, ".png",
    arrayOf("https://a.basemaps.cartocdn.com/light_all/",
            "https://b.basemaps.cartocdn.com/light_all/",
            "https://c.basemaps.cartocdn.com/light_all/")
)

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

    var mapStyle by remember { mutableStateOf("voyager") }
    var isTreeDetailOpen by remember { mutableStateOf(false) }
    var isTreeListOpen by remember { mutableStateOf(false) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var isAccountSettingsOpen by remember { mutableStateOf(false) }
    var isAppSettingsOpen by remember { mutableStateOf(false) }
    var pinCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

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
            requestCurrentLocation(context, mapViewModel)
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
        mapViewRef.value?.controller?.animateTo(GeoPoint(mapCenter.first, mapCenter.second))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ---- OSMDroid Map ----
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(mapCenter.first, mapCenter.second))
                    setTileSource(getCartoVoyager())
                    mapViewRef.value = this
                }
            },
            update = { mapView ->
                // Clear existing overlays and re-add markers
                mapView.overlays.clear()

                // User location marker
                val userMarker = Marker(mapView).apply {
                    position = GeoPoint(userLocation.first, userLocation.second)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Você está por aqui! 📍"
                    icon = createCircleDrawable(context, android.graphics.Color.parseColor("#2563EB"), 12)
                }
                mapView.overlays.add(userMarker)

                // Tree markers
                val filteredTrees = mapViewModel.getFilteredTrees()
                for (tw in filteredTrees) {
                    val tree = tw.tree
                    val meta = getStatusMeta(tree.currentStatus)
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(tree.latitude, tree.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = tree.species
                        snippet = tree.name
                        icon = createEmojiDrawable(context, meta.emoji, tree.currentStatus == TreeStatus.PRONTO)
                        setOnMarkerClickListener { _, _ ->
                            mapViewModel.selectTree(tree)
                            isTreeDetailOpen = true
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }

                // Update tile source based on style
                val tileSource = when (mapStyle) {
                    "dark" -> getCartoDark()
                    "positron" -> getCartoPositron()
                    "osm" -> TileSourceFactory.MAPNIK
                    else -> getCartoVoyager()
                }
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User info
                Text("🍒", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        currentUser.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                        color = Stone900,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        "@${currentUser.username}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MamaoOrange,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Account settings
                IconButton(
                    onClick = { isAccountSettingsOpen = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Outlined.Person, null, tint = Stone700, modifier = Modifier.size(18.dp))
                }

                // App settings
                IconButton(
                    onClick = { isAppSettingsOpen = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Outlined.Settings, null, tint = Stone700, modifier = Modifier.size(18.dp))
                }
                
                Spacer(modifier = Modifier.width(4.dp))

                // Logout
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MamaoOrangeLight,
                    onClick = onLogout,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Logout, null, tint = MamaoOrange, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // ---- Pin overlay for adding tree ----
        if (isAddingTree && pinCoordinates != null) {
            // Center pin
            Box(
                modifier = Modifier.fillMaxSize(),
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
                                "Posicionar Fruteira",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = Stone950
                            )
                            Text(
                                "Arraste o mapa para posicionar a fruteira exatamente sob o pin, depois confirme.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MamaoOrange
                            )
                            Text(
                                "COORDS: ${String.format("%.5f", pinCoordinates!!.first)}, ${String.format("%.5f", pinCoordinates!!.second)}",
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

        // ---- GPS FAB ----
        FloatingActionButton(
            onClick = {
                requestCurrentLocation(context, mapViewModel)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .navigationBarsPadding(),
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Stone900
        ) {
            Icon(Icons.Outlined.MyLocation, contentDescription = "Minha localização")
        }

        // ---- Bottom Navigation ----
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explorar
                BottomNavItem(
                    icon = Icons.Outlined.Home,
                    label = "Explorar",
                    selected = !isTreeListOpen && !isAddingTree,
                    onClick = {
                        isTreeListOpen = false
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
                        val next = !isAddingTree
                        mapViewModel.setIsAddingTree(next)
                        if (next) {
                            mapViewModel.showToast("📍 Mova o mapa para posicionar a árvore sob o pin.")
                        } else {
                            pinCoordinates = null
                        }
                    }
                )
            }
        }

        // ---- Tree List Bottom Sheet ----
        if (isTreeListOpen) {
            TreeListSheet(
                mapViewModel = mapViewModel,
                creatorUsernames = creatorUsernames,
                onSelectTree = { tree ->
                    mapViewModel.selectTree(tree)
                    isTreeListOpen = false
                    isTreeDetailOpen = true
                },
                onDismiss = { isTreeListOpen = false }
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
                onMapStyleChange = { mapStyle = it },
                onShowToast = { mapViewModel.showToast(it) },
                onDismiss = { isAppSettingsOpen = false }
            )
        }

        // ---- Toast ----
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .navigationBarsPadding()
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
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (selected) MamaoOrangeLight else Color.Transparent,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, contentDescription = label,
                    tint = if (selected) MamaoOrange else Stone400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold
            ),
            color = if (selected) MamaoOrange else Stone400
        )
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

// Helper to create an emoji marker drawable
private fun createEmojiDrawable(context: Context, emoji: String, isPulsing: Boolean): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (36 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = sizePx * 0.38f

    // Background circle
    val bgColor = when (emoji) {
        "🍎" -> android.graphics.Color.parseColor("#EF4444")
        "🍏" -> android.graphics.Color.parseColor("#22C55E")
        "🌸" -> android.graphics.Color.parseColor("#EC4899")
        else -> android.graphics.Color.parseColor("#78716C")
    }

    val bgPaint = android.graphics.Paint().apply {
        color = bgColor
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, bgPaint)

    // White border
    val borderPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2f * density
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, radius, borderPaint)

    // Emoji text
    val textPaint = android.graphics.Paint().apply {
        textSize = 16f * density
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }
    canvas.drawText(emoji, centerX, centerY + 6 * density, textPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

private fun requestCurrentLocation(context: Context, mapViewModel: MapViewModel) {
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
        }

        locationManager.requestSingleUpdate(
            LocationManager.GPS_PROVIDER,
            { location ->
                mapViewModel.setUserLocation(location.latitude, location.longitude)
            },
            null
        )
    } catch (e: Exception) {
        android.util.Log.e("MapScreen", "Error requesting location", e)
    }
}
