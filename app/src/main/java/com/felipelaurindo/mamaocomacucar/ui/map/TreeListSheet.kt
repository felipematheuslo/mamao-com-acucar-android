package com.felipelaurindo.mamaocomacucar.ui.map

import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.data.model.TreeItem
import com.felipelaurindo.mamaocomacucar.ui.map.components.StatusChip
import com.felipelaurindo.mamaocomacucar.ui.map.components.getStatusMeta
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.formatDistance
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes

@Composable
fun TreeListSheet(
    mapViewModel: MapViewModel,
    creatorUsernames: Map<String, String>,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(bottom = 64.dp)
                .navigationBarsPadding()
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Stone300, RoundedCornerShape(2.dp))
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔍", fontSize = 18.sp)
                            Text(
                                "Explorar Fruteiras",
                                style = MaterialTheme.typography.titleMedium,
                                color = Stone950
                            )
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Close, null, tint = Stone400, modifier = Modifier.size(16.dp))
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
                        placeholder = { Text("Buscar fruta, espécie ou endereço...", color = Stone400, fontSize = 13.sp) },
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
                        FilterChipItem("Todos", "todos", statusFilter) { mapViewModel.setStatusFilter(it) }
                        FilterChipItem("🍎 Maduro", "pronto", statusFilter) { mapViewModel.setStatusFilter(it) }
                        FilterChipItem("🍏 Verde", "crescendo", statusFilter) { mapViewModel.setStatusFilter(it) }
                        FilterChipItem("🌸 Florindo", "florindo", statusFilter) { mapViewModel.setStatusFilter(it) }
                        FilterChipItem("🌳 Vazio", "vazio", statusFilter) { mapViewModel.setStatusFilter(it) }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "${filteredTrees.size} fruteira(s) encontrada(s)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Stone400
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
                    if (filteredTrees.isEmpty()) {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🌿", fontSize = 32.sp)
                            Text(
                                "Nenhuma fruteira encontrada",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Stone600
                            )
                        }
                    } else {
                        filteredTrees.forEach { tw ->
                            val tree = tw.tree
                            val meta = getStatusMeta(tree.currentStatus)
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Status icon
                                    Surface(
                                        shape = CircleShape,
                                        color = meta.bgColor,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(id = getFruitDrawableRes(tree.species)),
                                                contentDescription = tree.species,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            tree.species,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                            color = Stone900,
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
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                            color = Stone700
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))
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
