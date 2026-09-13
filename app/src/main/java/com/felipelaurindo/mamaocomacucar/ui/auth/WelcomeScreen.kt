package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.R
import com.felipelaurindo.mamaocomacucar.ui.auth.components.GoogleSignInButton
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun WelcomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val isSubmittingGoogle by authViewModel.isSubmittingGoogle.collectAsState()
    val isSubmitting by authViewModel.isSubmitting.collectAsState()
    val loginError by authViewModel.loginError.collectAsState()

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
                .padding(horizontal = 28.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Logo Mamão
            Surface(
                shape = CircleShape,
                color = MamaoOrangeLight,
                modifier = Modifier.size(92.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_fruit_mamao),
                        contentDescription = "Logo Mamão com Açúcar",
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = "Mamão com Açúcar",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Stone950,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Slogan
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MamaoOrangeLight,
                border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.3f)),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "A rede social rica em vitaminas 🍊",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MamaoOrange,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtítulo descritivo
            Text(
                text = "Mapeie árvores frutíferas em espaços públicos, acompanhe a época de colheita e cultive a natureza urbana em comunidade.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = Stone500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pilares informativos / Destaques
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureItem(
                    emoji = "📍",
                    title = "Mapeie & Encontre",
                    description = "Ache mangueiras, amoreiras e fruteiras públicas perto de você."
                )
                FeatureItem(
                    emoji = "🌸",
                    title = "Acompanhe o Ciclo",
                    description = "Saiba quando a árvore está em flor, com frutos verdes ou pronta para colher."
                )
                FeatureItem(
                    emoji = "🌱",
                    title = "Comunidade & Conquistas",
                    description = "Cadastre novas árvores, ajude os vizinhos e ganhe selos de cultivador."
                )
            }

            // Mensagem de erro de login, se houver
            loginError?.let { errorMsg ->
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Rose50,
                    border = BorderStroke(1.dp, Rose200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodySmall,
                        color = Rose600,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Ações de Entrada
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão Google
                GoogleSignInButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    isLoading = isSubmittingGoogle,
                    enabled = !isSubmitting && !isSubmittingGoogle,
                    text = "Continuar com o Google"
                )

                // Botão Entrar com E-mail
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MamaoOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "ENTRAR COM E-MAIL",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chamada "Criar conta"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Não tem uma conta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone600
                )
                Text(
                    text = "Criar conta",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MamaoOrange,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(
    emoji: String,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Stone50,
        border = BorderStroke(1.dp, Stone200.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Stone200),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = emoji,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Stone900
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 16.sp
                    ),
                    color = Stone500
                )
            }
        }
    }
}
