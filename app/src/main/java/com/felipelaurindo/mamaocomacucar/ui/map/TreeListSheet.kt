package com.felipelaurindo.mamaocomacucar.ui.map

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    var geoSearchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val filteredTrees = remember(searchQuery, statusFilter, mapViewModel.trees.collectAsState().value) {
        mapViewModel.getFilteredTrees()
    }
    val nearbyTrees = remember(filteredTrees) {
        filteredTrees.filter { it.distance <= 20.0 }
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Unified Smart Search Bar (Tree + Geo Search)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            mapViewModel.setSearchQuery(query)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Stone950,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        placeholder = { Text("Buscar por fruta, espécie, bairro ou endereço...", color = Stone400, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MamaoOrange) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    mapViewModel.setSearchQuery("")
                                    focusManager.clearFocus()
                                }) {
                                    Icon(Icons.Outlined.Close, "Limpar busca", tint = Stone400, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchQuery.isNotBlank()) {
                                mapViewModel.searchLocation(searchQuery)
                            }
                            focusManager.clearFocus()
                        }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Stone950,
                            unfocusedTextColor = Stone950,
                            cursorColor = MamaoOrange,
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter chips
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val handleFilterClick: (String) -> Unit = { filter ->
                            if (isGuest && filter != "todos") {
                                onRequestAuth?.invoke("Cadastre-se gratuitamente para filtrar as árvores por fase de maturação.")
                            } else {
                                mapViewModel.setStatusFilter(filter)
                            }
                        }
                        FilterChipItem("Todas", "todos", statusFilter, handleFilterClick)
                        FilterChipItem("🍎 Maduro", "pronto", statusFilter, handleFilterClick)
                        FilterChipItem("🍏 Verde", "crescendo", statusFilter, handleFilterClick)
                        FilterChipItem("🌸 Florindo", "florindo", statusFilter, handleFilterClick)
                        FilterChipItem("🌳 Vazio", "vazio", statusFilter, handleFilterClick)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val nearbyCount = nearbyTrees.size

                    val countLabel = when {
                        isGuest && nearbyCount > 3 -> "Mostrando 3 de $nearbyCount fruteiras na sua região"
                        nearbyCount > 0 -> "$nearbyCount ${if (nearbyCount == 1) "fruteira encontrada" else "fruteiras encontradas"} na sua região (raio de 20 km)"
                        else -> "Nenhuma fruteira encontrada na sua região (raio de 20 km)"
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
                                "Não há fruteiras cadastradas em um raio de 20 km para este filtro.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Stone400
                            )
                        }
                    } else {
                        displayedTrees.forEach { tw ->
                            val tree = tw.tree
                            val username = creatorUsernames[tree.createdBy]
                                ?: "@${tree.createdByName.split(" ").first().lowercase()}"

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
    onClick: (String) -> Unit
) {
    val isSelected = currentFilter == value
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MamaoOrangeLight else Stone50,
        border = BorderStroke(1.dp, if (isSelected) MamaoOrange.copy(alpha = 0.3f) else Stone200),
        onClick = { onClick(value) }
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            ),
            color = if (isSelected) MamaoOrange else Stone500
        )
    }
}
