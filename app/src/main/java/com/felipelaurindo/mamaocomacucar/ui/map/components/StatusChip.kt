package com.felipelaurindo.mamaocomacucar.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.ui.theme.*

data class StatusMeta(
    val emoji: String,
    val label: String,
    val labelText: String,
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color
)

fun getStatusMeta(status: TreeStatus): StatusMeta {
    return when (status) {
        TreeStatus.PRONTO -> StatusMeta("🍎", "Maduro 🍎", "Maduro", StatusReadyBg, StatusReady, StatusReady.copy(alpha = 0.3f))
        TreeStatus.CRESCENDO -> StatusMeta("🍏", "Verde 🍏", "Verde", StatusGrowingBg, StatusGrowing, StatusGrowing.copy(alpha = 0.3f))
        TreeStatus.FLORINDO -> StatusMeta("🌸", "Florindo 🌸", "Florindo", StatusFloweringBg, StatusFlowering, StatusFlowering.copy(alpha = 0.3f))
        TreeStatus.VAZIO -> StatusMeta("🌳", "Vazio 🌳", "Vazio", StatusEmptyBg, StatusEmpty, StatusEmpty.copy(alpha = 0.3f))
    }
}

@Composable
fun StatusChip(
    status: TreeStatus,
    modifier: Modifier = Modifier,
    showEmoji: Boolean = true
) {
    val meta = getStatusMeta(status)

    Row(
        modifier = modifier
            .background(meta.bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, meta.borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showEmoji) {
            Text(meta.emoji, fontSize = 10.sp)
        }
        Text(
            meta.labelText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.3.sp
            ),
            color = meta.textColor
        )
    }
}
