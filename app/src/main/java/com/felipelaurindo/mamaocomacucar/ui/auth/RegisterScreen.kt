package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.normalizeUsername

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val isSubmitting by authViewModel.isSubmitting.collectAsState()
    val registerError by authViewModel.registerError.collectAsState()
    val registrationComplete by authViewModel.registrationComplete.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Reset state when navigating away
    DisposableEffect(Unit) {
        onDispose { authViewModel.resetRegistrationState() }
    }

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Mamão", style = MaterialTheme.typography.headlineMedium, color = Stone950)
                Text(" com Açúcar", style = MaterialTheme.typography.headlineMedium, color = MamaoOrange)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register Card
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
                    if (registrationComplete) {
                        // Success State
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Emerald50,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = Emerald600,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            Text(
                                "Confirme seu E-mail!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Stone950,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                "Enviamos um link de confirmação para o endereço $email.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                "Por favor, verifique sua caixa de entrada (e pasta de spam) e clique no link de ativação para poder entrar no app.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    authViewModel.resetRegistrationState()
                                    onNavigateToLogin()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                            ) {
                                Text("IR PARA O LOGIN", style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp)
                            }
                        }
                    } else {
                        // Registration Form
                        Text(
                            "NOVO CADASTRO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MamaoOrange,
                            letterSpacing = 2.sp
                        )

                        Text(
                            "Associe-se ao movimento",
                            style = MaterialTheme.typography.titleLarge,
                            color = Stone950
                        )
                        Text(
                            "Cataloge árvores frutíferas no bairro e ganhe medalhas de participação ativa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )

                        // Error
                        AnimatedVisibility(visible = registerError != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Rose50
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚠️ ", fontSize = 14.sp)
                                    Text(
                                        registerError ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Rose700
                                    )
                                }
                            }
                        }

                        // Display Name
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("NOME DE EXIBIÇÃO", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Felipe Oliveira", color = Stone400) },
                                leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Stone400) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
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

                        // Username
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("USERNAME", style = MaterialTheme.typography.labelSmall, color = MamaoOrange)
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = normalizeUsername(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("felipe_oliveira", color = Stone400) },
                                prefix = {
                                    Text(
                                        "@",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Stone400
                                    )
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MamaoOrange,
                                    unfocusedBorderColor = Stone200,
                                    focusedContainerColor = Stone50,
                                    unfocusedContainerColor = Stone50,
                                    focusedTextColor = MamaoOrange,
                                    unfocusedTextColor = MamaoOrange
                                )
                            )
                        }

                        // Email
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("E-MAIL", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("felipe@mamaocomacucar.com", color = Stone400) },
                                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Stone400) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
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

                        // Password
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("SENHA", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Mínimo 6 caracteres", color = Stone400) },
                                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Stone400) },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            null, tint = Stone400
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    authViewModel.registerWithEmail(displayName, username, email, password)
                                }),
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

                        // Register Button
                        Button(
                            onClick = { authViewModel.registerWithEmail(displayName, username, email, password) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = !isSubmitting && displayName.isNotBlank() && username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("CRIAR PERFIL", style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp)
                            }
                        }

                        HorizontalDivider(color = Stone100)

                        TextButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
                            Text("Já possui uma conta? ", style = MaterialTheme.typography.bodySmall, color = Stone600)
                            Text("Entrar", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = MamaoOrange)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
