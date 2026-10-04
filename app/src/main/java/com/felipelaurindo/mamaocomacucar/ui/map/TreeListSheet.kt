package com.felipelaurindo.mamaocomacucar.ui.map

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.data.getFruitDisplayName
import com.felipelaurindo.mamaocomacucar.data.model.TreeItem
import com.felipelaurindo.mamaocomacucar.ui.map.components.StatusChip
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.formatDistance
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes

enum class TreeSortOrder(val label: String) {
    DISTANCE("Menor distância"),
    RECENT_UPDATE("Mais recente")
}

@Composable
fun TreeListSheet(
    mapViewModel: MapViewModel,
    creatorUsernames: Map<String, String>,
    isGuest: Boolean = false,
    onRequestAuth: ((String) -> Unit)? = null,
    onSelectTree: (TreeItem) -> Unit,
    onDismiss: () -> Unit
) {
    val searchQuery by mapViewModel.searchQuery.collectAsState()
    val statusFilter by mapViewModel.statusFilter.collectAsState()

    var radiusInput by remember(isGuest) { mutableStateOf(if (isGuest) "2" else "20") }
    var sortOrder by remember { mutableStateOf(TreeSortOrder.DISTANCE) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    val filteredTrees = remember(searchQuery, statusFilter, mapViewModel.trees.collectAsState().value) {
        mapViewModel.getFilteredTrees()
    }

    val searchRadiusKm = radiusInput.toDoubleOrNull() ?: if (isGuest) 2.0 else 20.0

    val nearbyTrees = remember(filteredTrees, searchRadiusKm, sortOrder) {
        val withinRadius = filteredTrees.filter { it.distance <= searchRadiusKm }
        when (sortOrder) {
            TreeSortOrder.DISTANCE -> withinRadius.sortedBy { it.distance }
            TreeSortOrder.RECENT_UPDATE -> withinRadius.sortedByDescending { it.tree.lastActivityTimestamp }
        }
    }

    var isExpanded by remember { mutableStateOf(true) }

    val sheetHeightFraction by animateFloatAsState(
        targetValue = if (isExpanded) 0.85f else 0.52f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "sheetHeightFraction"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone950.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .statusBarsPadding()
                .padding(bottom = 64.dp)
                .navigationBarsPadding()
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Barra de arrasto interativa para estender / comprimir o card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                if (dragAmount < -15) {
                                    isExpanded = true
                                } else if (dragAmount > 20) {
                                    if (isExpanded) {
                                        isExpanded = false
                                    } else {
                                        onDismiss()
                                    }
                                }
                            }
                        }
                        .clickable { isExpanded = !isExpanded }
                        .padding(top = 10.dp, bottom = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(5.dp)
                            .background(Stone300, RoundedCornerShape(2.5.dp))
                    )
                }

                // Header
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { isExpanded = !isExpanded }
                        ) {
                            Text("🔍", fontSize = 18.sp)
                            Text(
                                "Explorar Fruteiras",
                                style = MaterialTheme.typography.titleMedium,
                                color = Stone950
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = if (isExpanded) "Comprimir lista" else "Estender lista",
                                    tint = Stone500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Outlined.Close, "Fechar", tint = Stone400, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Linha de controle: Raio de busca + Seletor de ordenação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Campo para digitar o tamanho do raio de busca (por padrão 20km)
                        Surface(
                            modifier = Modifier
                                .weight(0.95f)
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Stone50,
                            border = BorderStroke(1.dp, Stone200)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NearMe,
                                    contentDescription = "Raio de busca",
                                    tint = MamaoOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    "Raio:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Stone600
                                )
                                BasicTextField(
                                    value = radiusInput,
                                    onValueChange = { newValue ->
                                        val digits = newValue.filter { it.isDigit() }
                                        if (digits.length <= 4) {
                                            val num = digits.toIntOrNull() ?: 0
                                            if (isGuest && num > 2) {
                                                onRequestAuth?.invoke("Cadastre-se gratuitamente para expandir seu raio de busca além de 2 km.")
                                            } else {
                                                radiusInput = digits
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Stone900,
                                        textAlign = TextAlign.Center
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = true
                                )
                                Text(
                                    "km",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Stone500
                                )
                            }
                        }

                        // 2. Seletor para ordenar por menor distância ou atualizado mais recente
                        Box(modifier = Modifier.weight(1.05f)) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Stone50,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSortMenuExpanded) MamaoOrange.copy(alpha = 0.5f) else Stone200
                                ),
                                onClick = { isSortMenuExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (sortOrder == TreeSortOrder.DISTANCE) Icons.Outlined.Straighten else Icons.Outlined.History,
                                            contentDescription = null,
                                            tint = MamaoOrange,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = sortOrder.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Stone700,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = "Selecionar ordenação",
                                        tint = Stone400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Menor distância",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (sortOrder == TreeSortOrder.DISTANCE) FontWeight.Bold else FontWeight.Normal,
                                                color = if (sortOrder == TreeSortOrder.DISTANCE) MamaoOrange else Stone800
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Straighten,
                                            contentDescription = null,
                                            tint = if (sortOrder == TreeSortOrder.DISTANCE) MamaoOrange else Stone500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (sortOrder == TreeSortOrder.DISTANCE) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = MamaoOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        sortOrder = TreeSortOrder.DISTANCE
                                        isSortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Mais recente",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (sortOrder == TreeSortOrder.RECENT_UPDATE) FontWeight.Bold else FontWeight.Normal,
                                                color = if (sortOrder == TreeSortOrder.RECENT_UPDATE) MamaoOrange else Stone800
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.History,
                                            contentDescription = null,
                                            tint = if (sortOrder == TreeSortOrder.RECENT_UPDATE) MamaoOrange else Stone500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (sortOrder == TreeSortOrder.RECENT_UPDATE) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = MamaoOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        sortOrder = TreeSortOrder.RECENT_UPDATE
                                        isSortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Filter chips - distribuídos uniformemente na largura da tela sem precisar arrastar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val handleFilterClick: (String) -> Unit = { filter ->
                            if (isGuest && filter != "todos") {
                                onRequestAuth?.invoke("Cadastre-se gratuitamente para filtrar as árvores por fase de maturação.")
                            } else {
                                mapViewModel.setStatusFilter(filter)
                            }
                        }
                        FilterChipItem("Todas", "todos", statusFilter, handleFilterClick, Modifier.weight(1f))
                        FilterChipItem("🍎 Madura", "pronto", statusFilter, handleFilterClick, Modifier.weight(1f))
                        FilterChipItem("🍏 Verde", "crescendo", statusFilter, handleFilterClick, Modifier.weight(1f))
                        FilterChipItem("🌸 Flor", "florindo", statusFilter, handleFilterClick, Modifier.weight(1f))
                        FilterChipItem("🌳 Vazia", "vazio", statusFilter, handleFilterClick, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val nearbyCount = nearbyTrees.size
                    val radiusDisplay = if (searchRadiusKm % 1.0 == 0.0) searchRadiusKm.toInt().toString() else searchRadiusKm.toString()

                    val countLabel = when {
                        isGuest && nearbyCount > 3 -> "Mostrando 3 de $nearbyCount fruteiras na sua região"
                        nearbyCount > 0 -> "$nearbyCount ${if (nearbyCount == 1) "fruteira encontrada" else "fruteiras encontradas"}"
                        else -> "Nenhuma fruteira encontrada"
                    }

                    Text(
                        countLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Stone500
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                HorizontalDivider(color = Stone100)

                // Tree list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayedTrees = if (isGuest) nearbyTrees.take(3) else nearbyTrees
                    val radiusDisplay = if (searchRadiusKm % 1.0 == 0.0) searchRadiusKm.toInt().toString() else searchRadiusKm.toString()
                    if (displayedTrees.isEmpty()) {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🌿", fontSize = 32.sp)
                            Text(
                                "Nenhuma fruteira encontrada na sua região",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Stone600
                            )
                            Text(
                                "Não há fruteiras cadastradas em um raio de $radiusDisplay km para este filtro.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Stone400
                            )
                        }
                    } else {
                        displayedTrees.forEach { tw ->
                            val tree = tw.tree
                            val username = creatorUsernames[tree.createdBy]
                                ?: if (tree.createdByName.isNotBlank()) "@${tree.createdByName.split(" ").first().lowercase()}" else "@comunidade"

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Stone200),
                                onClick = { onSelectTree(tree) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Fruit avatar
                                    Surface(
                                        shape = CircleShape,
                                        color = MamaoOrangeLight,
                                        border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.2f)),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(id = getFruitDrawableRes(tree.species)),
                                                contentDescription = tree.species,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            getFruitDisplayName(tree.species),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Stone950,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            tree.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Stone500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                username,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MamaoOrange
                                            )
                                            StatusChip(status = tree.currentStatus)
                                        }
                                    }

                                    // Distance
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            formatDistance(tw.distance),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Stone950
                                        )
                                        Text(
                                            "de distância",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                            color = Stone400
                                        )
                                    }
                                }
                            }
                        }

                        if (isGuest) {
                            val hiddenCount = (nearbyTrees.size - displayedTrees.size).coerceAtLeast(0)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(18.dp),
                                color = MamaoOrangeLight,
                                border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("🔒", fontSize = 16.sp)
                                        Text(
                                            if (hiddenCount > 0) "Mais $hiddenCount ${if (hiddenCount == 1) "fruteira" else "fruteiras"} na região"
                                            else "Explore todas as fruteiras",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Stone900
                                        )
                                    }
                                    Text(
                                        "Cadastre-se gratuitamente para ver a lista completa de árvores frutíferas e a distância até elas.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = Stone600,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = {
                                            onRequestAuth?.invoke("Cadastre-se gratuitamente para ver a lista completa de árvores frutíferas e a distância exata até elas.")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                                    ) {
                                        Text(
                                            "Desbloquear Lista Completa 🍊",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    value: String,
    currentFilter: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentFilter == value
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MamaoOrangeLight else Stone50,
        border = BorderStroke(1.dp, if (isSelected) MamaoOrange.copy(alpha = 0.4f) else Stone200),
        onClick = { onClick(value) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                ),
                color = if (isSelected) MamaoOrange else Stone600
            )
        }
    }
}
