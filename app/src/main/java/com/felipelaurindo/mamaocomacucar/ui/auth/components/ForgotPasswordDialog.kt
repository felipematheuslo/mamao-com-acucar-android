package com.felipelaurindo.mamaocomacucar.ui.auth.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun ForgotPasswordDialog(
    initialEmailOrUsername: String = "",
    isLoading: Boolean,
    errorMessage: String?,
    isSuccess: Boolean,
    onSendReset: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var emailOrUsername by remember(initialEmailOrUsername) {
        mutableStateOf(initialEmailOrUsername)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable(enabled = false, onClick = {}),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MamaoOrangeLight,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🔑", fontSize = 20.sp)
                                }
                            }
                            Text(
                                "Recuperar Senha",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Stone900
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Stone100, CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Fechar",
                                tint = Stone600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (isSuccess) {
                        // Success state
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Emerald50,
                            border = BorderStroke(1.dp, Emerald600.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✉️ ", fontSize = 18.sp)
                                    Text(
                                        "E-mail enviado!",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Emerald600
                                    )
                                }
                                Text(
                                    "Enviamos um link de redefinição de senha para a conta informada. Por favor, confira sua caixa de entrada e a pasta de spam.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone700
                                )
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                        ) {
                            Text(
                                "ENTENDI",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }
                    } else {
                        // Form state
                        Text(
                            "Digite o e-mail ou nome de usuário associado à sua conta. Enviaremos um link seguro para você redefinir sua senha.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone600
                        )

                        // Error message
                        AnimatedVisibility(visible = errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Rose50,
                                border = BorderStroke(1.dp, Rose200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "⚠️ ${errorMessage ?: ""}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Rose700,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Input field
                        OutlinedTextField(
                            value = emailOrUsername,
                            onValueChange = { emailOrUsername = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("E-mail ou @username", color = Stone400) },
                            leadingIcon = {
                                Icon(Icons.Outlined.Email, contentDescription = null, tint = Stone400)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (emailOrUsername.isNotBlank() && !isLoading) {
                                        onSendReset(emailOrUsername)
                                    }
                                }
                            ),
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Stone200),
                                enabled = !isLoading
                            ) {
                                Text(
                                    "Cancelar",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Stone600
                                )
                            }

                            Button(
                                onClick = { onSendReset(emailOrUsername) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp),
                                enabled = !isLoading && emailOrUsername.isNotBlank(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MamaoOrange,
                                    disabledContainerColor = MamaoOrange.copy(alpha = 0.35f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Enviar Link",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
