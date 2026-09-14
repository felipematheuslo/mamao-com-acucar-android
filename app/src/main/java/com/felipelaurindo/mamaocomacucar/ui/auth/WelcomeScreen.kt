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
    onContinueAsGuest: () -> Unit,
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
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Header: Emblema do mamão sutil e equilibrado
            Surface(
                shape = CircleShape,
                color = MamaoOrangeLight,
                border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.2f)),
                modifier = Modifier.size(76.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_fruit_mamao),
                        contentDescription = "Logo Mamão com Açúcar",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título principal
            Text(
                text = "Mamão com Açúcar",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    fontSize = 24.sp
                ),
                color = Stone950,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Badge da comunidade
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MamaoOrangeLight,
                border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "A rede social rica em vitaminas 🍊",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = MamaoOrange,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtítulo fluído e acolhedor
            Text(
                text = "Mapeie árvores frutíferas públicas, acompanhe as épocas de colheita e cultive a cidade em comunidade.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Stone500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Centro: Lista limpa e minimalista de benefícios (sem caixas pesadas repetidas)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BenefitItem(
                    emoji = "📍",
                    title = "Mapeie & Encontre",
                    description = "Ache mangueiras, amoreiras e pitangueiras perto de você."
                )
                BenefitItem(
                    emoji = "🌸",
                    title = "Acompanhe o Ciclo",
                    description = "Saiba quando estão em flor, frutos verdes ou maduros."
                )
                BenefitItem(
                    emoji = "🌱",
                    title = "Colha em Comunidade",
                    description = "Descubra safras urbanas e ganhe selos de cultivador."
                )
            }

            // Mensagem de erro de autenticação (se houver)
            loginError?.let { errorMsg ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Rose50,
                    border = BorderStroke(1.dp, Rose200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Rose600,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Ações: Estrutura refinada com clara hierarquia visual
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Explorar sem conta (Botão convidativo em destaque)
                Surface(
                    onClick = onContinueAsGuest,
                    shape = RoundedCornerShape(14.dp),
                    color = MamaoOrangeLight,
                    border = BorderStroke(1.2.dp, MamaoOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Explorar o mapa sem conta 🌿",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MamaoOrange
                        )
                    }
                }

                // 2. Google Sign-In
                GoogleSignInButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    isLoading = isSubmittingGoogle,
                    enabled = !isSubmitting && !isSubmittingGoogle,
                    text = "Continuar com o Google",
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Divisor suave
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = Stone200.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "ou com seu e-mail",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                        color = Stone400,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = Stone200.copy(alpha = 0.8f)
                    )
                }

                // 4. Entrar com e-mail (Outlined suave)
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Stone200),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Stone700
                    )
                ) {
                    Text(
                        text = "Entrar com e-mail",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = Stone700
                    )
                }

                // 5. Rodapé: Criar conta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Novo por aqui? ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = Stone500
                    )
                    Text(
                        text = "Criar conta",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MamaoOrange,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitItem(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Stone100,
            modifier = Modifier.size(38.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = emoji, fontSize = 17.sp)
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = Stone900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = Stone500
            )
        }
    }
}
