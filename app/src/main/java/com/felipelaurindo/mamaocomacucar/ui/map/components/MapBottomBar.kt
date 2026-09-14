package com.felipelaurindo.mamaocomacucar.ui.map.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun MapBottomBar(
    isExploring: Boolean,
    isAddingTree: Boolean,
    isFruitCatalogOpen: Boolean,
    onExploreClick: () -> Unit,
    onToggleAddTreeClick: () -> Unit,
    onToggleFruitCatalogClick: () -> Unit,
    isBannerVisible: Boolean,
    modifier: Modifier = Modifier,
    adBannerContent: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, Stone200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Anúncio AdMob Ancorado (Docked)
            AnimatedVisibility(
                visible = isBannerVisible,
                enter = expandVertically(animationSpec = androidx.compose.animation.core.tween(180, easing = androidx.compose.animation.core.FastOutSlowInEasing)) + fadeIn(animationSpec = androidx.compose.animation.core.tween(120)),
                exit = shrinkVertically(animationSpec = androidx.compose.animation.core.tween(140, easing = androidx.compose.animation.core.FastOutLinearInEasing)) + fadeOut(animationSpec = androidx.compose.animation.core.tween(100))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Stone50)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    adBannerContent()
                }
            }

            // Barra de Navegação Inferior (3 Destinos com Mapear em Destaque)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Destino 1: Explorar (abre a lista de fruteiras)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    NavActionItem(
                        icon = Icons.Outlined.Explore,
                        label = "Explorar",
                        selected = isExploring,
                        onClick = onExploreClick
                    )
                }

                // Destino 2 (CTA Principal): Mapear Fruteira
                Box(
                    modifier = Modifier.weight(1.3f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = onToggleAddTreeClick,
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAddingTree) Stone900 else MamaoOrange,
                        shadowElevation = 4.dp,
                        modifier = Modifier.height(44.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isAddingTree) Icons.Outlined.Close else Icons.Outlined.Add,
                                contentDescription = if (isAddingTree) "Cancelar mapeamento" else "Mapear nova fruteira",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isAddingTree) "Cancelar" else "Mapear",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // Destino 3: Catálogo Botânico
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    NavActionItem(
                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                        label = "Catálogo",
                        selected = isFruitCatalogOpen,
                        onClick = onToggleFruitCatalogClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NavActionItem(
    icon: ImageVector,
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MamaoOrange else Stone400,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold
                ),
                color = if (selected) MamaoOrange else Stone500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MapBottomBarPreview() {
    MamaoComAcucarTheme {
        MapBottomBar(
            isExploring = false,
            isAddingTree = false,
            isFruitCatalogOpen = false,
            onExploreClick = {},
            onToggleAddTreeClick = {},
            onToggleFruitCatalogClick = {},
            isBannerVisible = true,
            adBannerContent = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp),
                    color = Stone100,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Espaço Anúncio AdMob Docked (320x50)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone500
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MapBottomBarExploringModePreview() {
    MamaoComAcucarTheme {
        MapBottomBar(
            isExploring = true,
            isAddingTree = false,
            isFruitCatalogOpen = false,
            onExploreClick = {},
            onToggleAddTreeClick = {},
            onToggleFruitCatalogClick = {},
            isBannerVisible = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MapBottomBarAddingModePreview() {
    MamaoComAcucarTheme {
        MapBottomBar(
            isExploring = false,
            isAddingTree = true,
            isFruitCatalogOpen = false,
            onExploreClick = {},
            onToggleAddTreeClick = {},
            onToggleFruitCatalogClick = {},
            isBannerVisible = false
        )
    }
}
