package com.felipelaurindo.mamaocomacucar.ui.map

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.data.ALLOWED_FRUITS
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.map.components.getStatusMeta
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes

@Composable
fun AddTreeDialog(
    coordinates: Pair<Double, Double>,
    currentUser: LoggedUser,
    mapViewModel: MapViewModel,
    onDismiss: () -> Unit
) {
    var selectedSpecies by remember { mutableStateOf("") }
    var referenceName by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TreeStatus.VAZIO) }
    var speciesSearchQuery by remember { mutableStateOf("") }
    var showSpeciesDropdown by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val filteredFruits = remember(speciesSearchQuery) {
        if (speciesSearchQuery.isBlank()) ALLOWED_FRUITS
        else ALLOWED_FRUITS.filter {
            it.lowercase().contains(speciesSearchQuery.lowercase())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌳", fontSize = 22.sp)
                        Column {
                            Text(
                                "Catalogar Fruteira",
                                style = MaterialTheme.typography.titleMedium,
                                color = Stone950
                            )
                            Text(
                                "Adicione um pé de árvore frutífera ao acervo",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Stone400
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Close, null, tint = Stone400, modifier = Modifier.size(16.dp))
                    }
                }

                HorizontalDivider(color = Stone100)

                // Species selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "ESPÉCIE DA FRUTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MamaoOrange,
                        letterSpacing = 2.sp
                    )
                    OutlinedTextField(
                        value = if (selectedSpecies.isNotEmpty() && !showSpeciesDropdown) selectedSpecies
                               else speciesSearchQuery,
                        onValueChange = {
                            speciesSearchQuery = it
                            selectedSpecies = ""
                            showSpeciesDropdown = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar espécie...", color = Stone400) },
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Stone400) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50
                        )
                    )

                    // Quick suggestion chips for popular fruits
                    val quickSuggestions = listOf("Pitanga 🍒", "Amora 🫐", "Goiaba 🍐", "Mangueira 🥭", "Mamão 🍈", "Jabuticaba 🟣", "Pitomba 🟡")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickSuggestions.forEach { item ->
                            val cleanName = item.split(" ").first()
                            val isSelected = selectedSpecies.equals(cleanName, ignoreCase = true)
                            val fruitRes = getFruitDrawableRes(cleanName)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MamaoOrangeLight else Stone50,
                                border = BorderStroke(1.dp, if (isSelected) MamaoOrange else Stone200),
                                onClick = {
                                    selectedSpecies = cleanName
                                    speciesSearchQuery = cleanName
                                    showSpeciesDropdown = false
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = fruitRes),
                                        contentDescription = cleanName,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        cleanName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                                        ),
                                        color = if (isSelected) MamaoOrange else Stone600
                                    )
                                }
                            }
                        }
                    }

                    // Dropdown
                    if (showSpeciesDropdown && filteredFruits.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Stone200),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                filteredFruits.forEach { fruit ->
                                    Text(
                                        text = "🍃 $fruit",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedSpecies = fruit
                                                speciesSearchQuery = fruit
                                                showSpeciesDropdown = false
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Stone900
                                    )
                                }
                            }
                        }
                    }
                }

                // Reference name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "REFERÊNCIA / NOME",
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone400,
                        letterSpacing = 2.sp
                    )
                    OutlinedTextField(
                        value = referenceName,
                        onValueChange = { referenceName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Pé de Manga da esquina, Praça João...", color = Stone400) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50
                        )
                    )
                }

                // Status selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "ESTADO ATUAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone400,
                        letterSpacing = 2.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TreeStatus.entries.forEach { status ->
                            val meta = getStatusMeta(status)
                            val isSelected = selectedStatus == status

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) meta.bgColor else Stone50,
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) meta.textColor else Stone200
                                ),
                                onClick = { selectedStatus = status }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(meta.emoji, fontSize = 16.sp)
                                    Text(
                                        meta.labelText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isSelected) meta.textColor else Stone500,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Coordinates display
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Stone50,
                    border = BorderStroke(1.dp, Stone200)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📍", fontSize = 14.sp)
                        Column {
                            Text(
                                "Coordenadas do Pin",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Stone700
                            )
                            Text(
                                "${String.format("%.5f", coordinates.first)}, ${String.format("%.5f", coordinates.second)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = Stone500
                            )
                        }
                    }
                }

                // Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = {
                            isSubmitting = true
                            mapViewModel.addTree(
                                species = selectedSpecies,
                                name = referenceName,
                                status = selectedStatus,
                                latitude = coordinates.first,
                                longitude = coordinates.second,
                                createdBy = currentUser.uid,
                                createdByName = currentUser.displayName
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange),
                        enabled = selectedSpecies.isNotBlank() && !isSubmitting
                    ) {
                        Text(
                            if (isSubmitting) "Salvando..." else "Salvar 🌳",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
