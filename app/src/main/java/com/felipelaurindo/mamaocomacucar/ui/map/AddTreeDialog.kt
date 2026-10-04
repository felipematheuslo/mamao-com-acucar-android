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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.onFocusChanged
import com.felipelaurindo.mamaocomacucar.data.ALLOWED_FRUITS
import com.felipelaurindo.mamaocomacucar.data.findFruitDefinition
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.map.components.getStatusMeta
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes
import java.text.Normalizer

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
    var showSuggestions by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val filteredFruits = remember(speciesSearchQuery) {
        val queryNorm = Normalizer.normalize(speciesSearchQuery, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
        if (queryNorm.isBlank()) ALLOWED_FRUITS
        else ALLOWED_FRUITS.filter { fruit ->
            val fruitNorm = Normalizer.normalize(fruit, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                .lowercase()
            fruitNorm.contains(queryNorm) || findFruitDefinition(fruit)?.aliases?.any { alias ->
                val aliasNorm = Normalizer.normalize(alias, Normalizer.Form.NFD)
                    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                    .lowercase()
                aliasNorm.contains(queryNorm)
            } == true
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
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showSuggestions = false
                },
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showSuggestions = false
                    }
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
                                "Cadastrar Fruteira",
                                style = MaterialTheme.typography.titleMedium,
                                color = Stone950
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Close, null, tint = Stone400, modifier = Modifier.size(16.dp))
                    }
                }

                HorizontalDivider(color = Stone100)

                // Species selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ESPÉCIE DA FRUTA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MamaoOrange,
                            letterSpacing = 2.sp
                        )
                        if (selectedSpecies.isNotBlank()) {
                            Text(
                                "Selecionada ✓",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MamaoGreen
                            )
                        }
                    }

                    OutlinedTextField(
                        value = if (selectedSpecies.isNotEmpty() && !showSuggestions) selectedSpecies else speciesSearchQuery,
                        onValueChange = {
                            speciesSearchQuery = it
                            selectedSpecies = ""
                            showSuggestions = it.isNotBlank()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused && speciesSearchQuery.isNotBlank() && selectedSpecies.isBlank()) {
                                    showSuggestions = true
                                }
                            },
                        placeholder = {
                            Text(
                                "Buscar fruta no catálogo (ex: Manga, Amora)...",
                                color = Stone400,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            if (selectedSpecies.isNotEmpty() && !showSuggestions) {
                                Image(
                                    painter = painterResource(id = getFruitDrawableRes(selectedSpecies)),
                                    contentDescription = selectedSpecies,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(Icons.Outlined.Search, contentDescription = null, tint = Stone400)
                            }
                        },
                        trailingIcon = {
                            if (speciesSearchQuery.isNotEmpty() || selectedSpecies.isNotEmpty()) {
                                IconButton(onClick = {
                                    speciesSearchQuery = ""
                                    selectedSpecies = ""
                                    showSuggestions = false
                                }) {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = "Limpar busca",
                                        tint = Stone400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Stone900,
                            unfocusedTextColor = Stone900,
                            cursorColor = MamaoOrange,
                            focusedBorderColor = if (selectedSpecies.isNotEmpty()) MamaoGreen else MamaoOrange,
                            unfocusedBorderColor = if (selectedSpecies.isNotEmpty()) MamaoGreen.copy(alpha = 0.5f) else Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50
                        )
                    )
                    // Dynamic suggestions list - only shown while typing
                    if (showSuggestions && speciesSearchQuery.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Stone200),
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (filteredFruits.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nenhuma espécie encontrada para \"$speciesSearchQuery\".\nTente outro termo ou sinônimo.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = Stone500,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    filteredFruits.forEachIndexed { index, fruit ->
                                        val fruitRes = getFruitDrawableRes(fruit)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedSpecies = fruit
                                                    speciesSearchQuery = fruit
                                                    showSuggestions = false
                                                }
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = fruitRes),
                                                contentDescription = fruit,
                                                modifier = Modifier.size(26.dp)
                                            )
                                            Text(
                                                text = fruit,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = Stone900
                                            )
                                        }
                                        if (index < filteredFruits.lastIndex) {
                                            HorizontalDivider(color = Stone100, thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Reference name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "NOME OU REFERÊNCIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone400,
                        letterSpacing = 2.sp
                    )
                    OutlinedTextField(
                        value = referenceName,
                        onValueChange = {
                            showSuggestions = false
                            referenceName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (it.isFocused) showSuggestions = false },
                        placeholder = { Text("Ex: Pé de manga na esquina da praça", color = Stone400) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Stone900,
                            unfocusedTextColor = Stone900,
                            cursorColor = MamaoOrange,
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
                        "FASE/ESTADO ATUAL",
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
                                onClick = {
                                    selectedStatus = status
                                    showSuggestions = false
                                }
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
                                createdBy = currentUser.uid
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange),
                        enabled = selectedSpecies.isNotBlank() && !isSubmitting
                    ) {
                        Text(
                            if (isSubmitting) "Salvando..." else "Salvar Fruteira",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
