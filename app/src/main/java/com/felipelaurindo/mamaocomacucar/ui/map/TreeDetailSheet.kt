package com.felipelaurindo.mamaocomacucar.ui.map

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.data.model.CommentUpdate
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.model.TreeItem
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.map.components.StatusChip
import com.felipelaurindo.mamaocomacucar.ui.map.components.getStatusMeta
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.getFruitDrawableRes
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TreeDetailSheet(
    tree: TreeItem,
    updates: List<CommentUpdate>,
    currentUser: LoggedUser,
    creatorUsernames: Map<String, String>,
    mapViewModel: MapViewModel,
    onDismiss: () -> Unit
) {
    var selectedPhase by remember { mutableStateOf<TreeStatus?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val statusMeta = getStatusMeta(tree.currentStatus)
    val creatorUsername = mapViewModel.getCreatorUsername(tree.createdBy, tree.createdByName)

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
                .fillMaxHeight(0.75f)
                .padding(bottom = 64.dp)
                .navigationBarsPadding()
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(MamaoOrange, MamaoOrangeDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = getFruitDrawableRes(tree.species)),
                                        contentDescription = tree.species,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        tree.species,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    tree.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Outlined.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusChip(status = tree.currentStatus)
                            Text(
                                "por $creatorUsername",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "COORDS: ${String.format("%.5f", tree.latitude)}, ${String.format("%.5f", tree.longitude)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace, fontSize = 9.sp
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Phase Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "ATUALIZAR FASE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone400,
                            letterSpacing = 2.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TreeStatus.entries.forEach { status ->
                                val meta = getStatusMeta(status)
                                val isSelected = selectedPhase == status
                                val isCurrent = tree.currentStatus == status

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) meta.bgColor else Stone50,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) meta.textColor else Stone200
                                    ),
                                    onClick = { selectedPhase = status }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(meta.emoji, fontSize = 20.sp)
                                        Text(
                                            meta.labelText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isSelected) meta.textColor else Stone500,
                                            textAlign = TextAlign.Center
                                        )
                                        if (isCurrent) {
                                            Text(
                                                "Atual",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                color = Stone400
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedPhase != null && selectedPhase != tree.currentStatus) {
                            Button(
                                onClick = {
                                    isSubmitting = true
                                    mapViewModel.submitReport(
                                        treeId = tree.id,
                                        status = selectedPhase!!,
                                        createdBy = currentUser.uid,
                                        createdByName = currentUser.displayName
                                    )
                                    isSubmitting = false
                                    selectedPhase = null
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange),
                                enabled = !isSubmitting
                            ) {
                                Text(
                                    if (isSubmitting) "Enviando..." else "Confirmar Nova Fase ✅",
                                    style = MaterialTheme.typography.labelMedium,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // Timeline
                    if (updates.isNotEmpty()) {
                        HorizontalDivider(color = Stone100)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "HISTÓRICO DE ATUALIZAÇÕES",
                                style = MaterialTheme.typography.labelSmall,
                                color = Stone400,
                                letterSpacing = 2.sp
                            )

                            updates.forEach { update ->
                                val updateMeta = getStatusMeta(update.statusAtReport)
                                val updateUsername = creatorUsernames[update.createdBy]
                                    ?: "@${update.createdByName.split(" ").first().lowercase()}"

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Stone50,
                                    border = BorderStroke(1.dp, Stone200)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Status indicator
                                        Surface(
                                            shape = CircleShape,
                                            color = updateMeta.bgColor,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(updateMeta.emoji, fontSize = 14.sp)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    update.createdByName,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Stone900
                                                )
                                                Text(
                                                    formatDate(update.createdAt),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = Stone400
                                                )
                                            }
                                            Text(
                                                updateUsername,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MamaoOrange
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                update.comment,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                                                color = Stone600
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty state
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Stone50
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🌿", fontSize = 28.sp)
                                Text(
                                    "Ainda sem atualizações",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Stone600
                                )
                                Text(
                                    "Seja o primeiro a informar a fase desta fruteira!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Stone400,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoString.substringBefore(".").substringBefore("Z"))
        outputFormat.format(date!!)
    } catch (e: Exception) {
        isoString.take(10)
    }
}
