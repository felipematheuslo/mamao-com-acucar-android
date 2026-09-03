package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.felipelaurindo.mamaocomacucar.R
import com.felipelaurindo.mamaocomacucar.ui.auth.components.ForgotPasswordDialog
import com.felipelaurindo.mamaocomacucar.ui.auth.components.GoogleSignInButton
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit
) {
    val isSubmitting by authViewModel.isSubmitting.collectAsState()
    val isSubmittingGoogle by authViewModel.isSubmittingGoogle.collectAsState()
    val loginError by authViewModel.loginError.collectAsState()
    val showVerificationSent by authViewModel.showVerificationSent.collectAsState()
    val resendSuccess by authViewModel.resendSuccess.collectAsState()

    val isSendingPasswordReset by authViewModel.isSendingPasswordReset.collectAsState()
    val passwordResetError by authViewModel.passwordResetError.collectAsState()
    val passwordResetSuccess by authViewModel.passwordResetSuccess.collectAsState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

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
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_fruit_mamao),
                contentDescription = "Logo Mamão com Açúcar",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title — clean like Duolingo's "Entrar"
            Text(
                "Entrar",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Stone950
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Bem-vindo(a) de volta ao Mamão com Açúcar!",
                style = MaterialTheme.typography.bodySmall,
                color = Stone500
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            AnimatedVisibility(visible = loginError != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MamaoOrangeLight
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("✉️ ", fontSize = 16.sp)
                            Text(
                                loginError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Stone900
                            )
                        }
                        if (showVerificationSent) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (isSubmitting) "Enviando..." else "Não recebeu o e-mail? Toque para reenviar",
                                modifier = Modifier.clickable(
                                    enabled = !isSubmitting
                                ) {
                                    authViewModel.resendVerificationEmail(email, password)
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MamaoOrange
                            )
                        }
                    }
                }
            }

            // Resend success
            AnimatedVisibility(visible = resendSuccess) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald50
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅ ", fontSize = 14.sp)
                        Text(
                            "E-mail de confirmação reenviado com sucesso! Cheque sua caixa de entrada.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Emerald600
                        )
                    }
                }
            }

            // Email field — directly on the page, no card
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; authViewModel.clearLoginError() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("E-mail ou nome de usuário", color = Stone400)
                },
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

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; authViewModel.clearLoginError() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Sua senha", color = Stone400) },
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
                        if (email.isNotBlank() && password.isNotBlank()) {
                            authViewModel.loginWithEmail(email, password)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Links: Criar conta & Esqueceu a senha?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Criar conta",
                    modifier = Modifier.clickable(onClick = onNavigateToRegister),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MamaoOrange
                )
                Text(
                    text = "Esqueceu a senha?",
                    modifier = Modifier.clickable {
                        authViewModel.clearPasswordResetState()
                        showForgotPasswordDialog = true
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MamaoOrange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button — full width, prominent
            Button(
                onClick = { authViewModel.loginWithEmail(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSubmitting && !isSubmittingGoogle && email.isNotBlank() && password.isNotBlank(),
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
                        "ENTRAR",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Or divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
                Text(
                    text = "OU",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Stone400,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign In button
            GoogleSignInButton(
                onClick = { authViewModel.loginWithGoogle(context) },
                isLoading = isSubmittingGoogle,
                enabled = !isSubmitting && !isSubmittingGoogle,
                text = "Entrar com o Google"
            )

            Spacer(modifier = Modifier.navigationBarsPadding().height(32.dp))
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                initialEmailOrUsername = email,
                isLoading = isSendingPasswordReset,
                errorMessage = passwordResetError,
                isSuccess = passwordResetSuccess,
                onSendReset = { authViewModel.sendPasswordResetEmail(it) },
                onDismiss = {
                    showForgotPasswordDialog = false
                    authViewModel.clearPasswordResetState()
                }
            )
        }
    }
}
