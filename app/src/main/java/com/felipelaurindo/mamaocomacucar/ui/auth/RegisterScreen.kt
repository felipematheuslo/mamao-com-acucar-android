package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            if (registrationComplete) {
                // ---- Success State ----
                Spacer(modifier = Modifier.height(48.dp))

                Surface(
                    shape = CircleShape,
                    color = Emerald50,
                    modifier = Modifier.size(80.dp)
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

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Confirme seu E-mail!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Stone950,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Enviamos um link de confirmação para $email.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone500,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Verifique sua caixa de entrada (e a pasta de spam) e clique no link para ativar sua conta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        authViewModel.resetRegistrationState()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                ) {
                    Text(
                        "IR PARA O LOGIN",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color.White
                    )
                }
            } else {
                // ---- Registration Form ----

                // Emoji logo
                Text("🌱", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Criar conta",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Stone950
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Mapeie fruteiras urbanas e ganhe medalhas",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone500
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Error
                AnimatedVisibility(visible = registerError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
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
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Rose700
                            )
                        }
                    }
                }

                // Display Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nome de exibição", color = Stone400) },
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Stone400) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = normalizeUsername(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("nome_de_usuario", color = Stone400) },
                    prefix = {
                        Text(
                            "@",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MamaoOrange
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MamaoOrange,
                        unfocusedTextColor = MamaoOrange,
                        cursorColor = MamaoOrange,
                        focusedBorderColor = MamaoOrange,
                        unfocusedBorderColor = Stone200,
                        focusedContainerColor = Stone50,
                        unfocusedContainerColor = Stone50
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("E-mail", color = Stone400) },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Stone400) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Senha (mínimo 6 caracteres)", color = Stone400) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Stone400) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                null, tint = Stone400
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
                            if (displayName.isNotBlank() && username.isNotBlank() &&
                                email.isNotBlank() && password.isNotBlank()
                            ) {
                                authViewModel.registerWithEmail(displayName, username, email, password)
                            }
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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

                Spacer(modifier = Modifier.height(24.dp))

                // Register Button
                Button(
                    onClick = {
                        authViewModel.registerWithEmail(displayName, username, email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isSubmitting && displayName.isNotBlank() &&
                            username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MamaoOrange,
                        disabledContainerColor = MamaoOrange.copy(alpha = 0.35f)
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "CRIAR PERFIL",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom login link
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Stone100
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Já tem uma conta? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )
                    Text(
                        "ENTRAR",
                        modifier = Modifier.clickable(onClick = onNavigateToLogin),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MamaoOrange
                    )
                }
            }
        }
    }
}
