package com.felipelaurindo.mamaocomacucar.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun AppSettingsSheet(
    currentMapStyle: String,
    onMapStyleChange: (String) -> Unit,
    onShowToast: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val mapStyles = listOf(
        MapStyleOption("voyager", "Voyager (Claro)", "🛰️"),
        MapStyleOption("dark", "Dark Matter (Escuro)", "🌌"),
        MapStyleOption("positron", "Positron (Claro)", "❄️"),
        MapStyleOption("osm", "OSM Clássico", "🗺️")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
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
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        Text("⚙️", fontSize = 20.sp)
                        Text(
                            "Ajustes do App",
                            style = MaterialTheme.typography.titleSmall,
                            color = Stone950
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Close, null, tint = Stone400, modifier = Modifier.size(16.dp))
                    }
                }

                HorizontalDivider(color = Stone100)

                // Map Style Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "ESTILO DO MAPA (TEMA)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone400,
                        letterSpacing = 2.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // 2x2 grid
                        for (row in mapStyles.chunked(2)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (style in row) {
                                    val isSelected = currentMapStyle == style.id
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) MamaoOrangeLight else Color.White,
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) MamaoOrange else Stone200
                                        ),
                                        onClick = {
                                            onMapStyleChange(style.id)
                                            onShowToast("🗺️ Tema de mapa alterado para: ${style.label}")
                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                style.label,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                                color = Stone900
                                            )
                                            Text(style.emoji, fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Confirm button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Stone900)
                ) {
                    Text("Confirmar Ajustes", style = MaterialTheme.typography.labelMedium)
                }

                Text(
                    text = "Mamão com Açúcar • v1.0",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                    color = Stone400,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private data class MapStyleOption(
    val id: String,
    val label: String,
    val emoji: String
)

