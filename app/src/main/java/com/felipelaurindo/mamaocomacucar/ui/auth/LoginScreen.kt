package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit
) {
    val isSubmitting by authViewModel.isSubmitting.collectAsState()
    val loginError by authViewModel.loginError.collectAsState()
    val showVerificationSent by authViewModel.showVerificationSent.collectAsState()
    val resendSuccess by authViewModel.resendSuccess.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Brand Icon
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MamaoOrange,
                shadowElevation = 8.dp,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Explore,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brand Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Mamão",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Stone950
                )
                Text(
                    " com Açúcar",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MamaoOrange
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section Label
                    Text(
                        "ENTRAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MamaoOrange,
                        letterSpacing = 2.sp
                    )

                    // Title
                    Text(
                        "Acesse seu perfil",
                        style = MaterialTheme.typography.titleLarge,
                        color = Stone950
                    )
                    Text(
                        "Acompanhe novas marcações ecológicas comunitárias.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )

                    // Error message
                    AnimatedVisibility(visible = loginError != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MamaoOrangeLight,
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = Brush.linearGradient(listOf(MamaoOrangeContainer, MamaoOrangeContainer))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("✉️ ", fontSize = 16.sp)
                                    Text(
                                        loginError ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Stone900
                                    )
                                }
                                if (showVerificationSent) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { authViewModel.resendVerificationEmail(email, password) },
                                        enabled = !isSubmitting
                                    ) {
                                        Text(
                                            if (isSubmitting) "Enviando..." else "Não recebeu? Clique para reenviar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MamaoOrange
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Resend success
                    AnimatedVisibility(visible = resendSuccess) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald50
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅ ", fontSize = 14.sp)
                                Text(
                                    "E-mail de confirmação reenviado com sucesso!",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Emerald600
                                )
                            }
                        }
                    }

                    // Email field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "E-MAIL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone400
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; authViewModel.clearLoginError() },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("felipe@mamaocomacucar.com", color = Stone400) },
                            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Stone400) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
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

                    // Password field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "SENHA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone400
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; authViewModel.clearLoginError() },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Mínimo 6 caracteres", color = Stone400) },
                            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Stone400) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Outlined.VisibilityOff
                                        else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                        tint = Stone400
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    authViewModel.loginWithEmail(email, password)
                                }
                            ),
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

                    // Login Button
                    Button(
                        onClick = { authViewModel.loginWithEmail(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !isSubmitting && email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "ACESSAR MAPA",
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Divider + Register link
                    HorizontalDivider(color = Stone100)

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Não possui uma conta? ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone600
                        )
                        Text(
                            "Criar conta",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MamaoOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
