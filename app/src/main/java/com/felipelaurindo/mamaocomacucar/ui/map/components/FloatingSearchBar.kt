package com.felipelaurindo.mamaocomacucar.ui.map.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun FloatingSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: (String) -> Unit = {},
    onProfileClick: () -> Unit,
    isGuest: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, Stone200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 6.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone de Busca clicável
            IconButton(
                onClick = {
                    if (query.isNotBlank()) {
                        onSearch(query)
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = MamaoOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Campo de Texto de Busca
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Buscar manga, pitanga, bairro...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Stone400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Stone900,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(MamaoOrange),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotBlank()) {
                                onSearch(query)
                                focusManager.clearFocus()
                            }
                        }
                    )
                )
            }

            // Ações do lado direito
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botão de Limpar busca (quando há texto digitado)
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onClearQuery()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Limpar busca",
                            tint = Stone500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Avatar / Perfil do Usuário
                Surface(
                    shape = CircleShape,
                    color = MamaoOrangeLight,
                    border = BorderStroke(1.5.dp, MamaoOrange.copy(alpha = 0.4f)),
                    onClick = onProfileClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isGuest) {
                            Text("🌱", fontSize = 16.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Meu Perfil",
                                tint = MamaoOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F4)
@Composable
private fun FloatingSearchBarEmptyPreview() {
    MamaoComAcucarTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FloatingSearchBar(
                query = "",
                onQueryChange = {},
                onClearQuery = {},
                onProfileClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F4)
@Composable
private fun FloatingSearchBarFilledPreview() {
    MamaoComAcucarTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FloatingSearchBar(
                query = "Amora",
                onQueryChange = {},
                onClearQuery = {},
                onProfileClick = {}
            )
        }
    }
}
