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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Mail
import com.felipelaurindo.mamaocomacucar.R
import com.felipelaurindo.mamaocomacucar.ui.auth.components.GoogleSignInButton
import com.felipelaurindo.mamaocomacucar.ui.theme.*

private val FrauncesFontFamily = FontFamily(
    Font(R.font.fraunces, FontWeight.Bold)
)

private val CaveatFontFamily = FontFamily(
    Font(R.font.caveat, FontWeight.Bold)
)

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF3EB),
                        Color(0xFFFFFAF5),
                        Color.White
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val screenHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .defaultMinSize(minHeight = screenHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Bloco Superior: Identidade, Slogan de Impacto e Proposta de Valor
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                // Emblema com Aura Solar Concêntrica
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(106.dp)
                ) {
                    // Halo solar externo translúcido
                    Surface(
                        shape = CircleShape,
                        color = MamaoOrange.copy(alpha = 0.08f),
                        modifier = Modifier.size(106.dp)
                    ) {}

                    // Halo médio
                    Surface(
                        shape = CircleShape,
                        color = MamaoOrangeLight,
                        border = BorderStroke(1.dp, MamaoOrange.copy(alpha = 0.2f)),
                        modifier = Modifier.size(92.dp)
                    ) {}

                    // Círculo central do mamão com sombra viva
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.5.dp, MamaoOrange.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                ambientColor = MamaoOrange.copy(alpha = 0.3f),
                                spotColor = MamaoOrange.copy(alpha = 0.25f)
                            )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_fruit_mamao),
                                contentDescription = "Logo Mamão com Açúcar",
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Título principal
                Text(
                    text = "Mamão com Açúcar",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        fontSize = 27.sp
                    ),
                    color = Stone950,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Slogan como Assinatura Manuscrita (diferenciação visual absoluta do título)
                Text(
                    text = "A rede social rica em vitaminas",
                    style = TextStyle(
                        fontFamily = CaveatFontFamily,
                        fontSize = 22.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = MamaoOrangeDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Frase de valor
                Text(
                    text = "Mapeie árvores frutíferas em locais públicos na sua cidade. Colha frutas e cultive em comunidade.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp
                    ),
                    color = Stone500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                // Mensagem de erro de autenticação (se houver)
                loginError?.let { errorMsg ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
            }

            // Bloco Central: Vitrine Botânica Viva (Frutas reais da comunidade)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                // Fita de frutas com efeito de pomar urbano
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FruitBadge(drawableId = R.drawable.ic_fruit_pitanga, name = "Pitanga")
                    Spacer(modifier = Modifier.width(10.dp))
                    FruitBadge(drawableId = R.drawable.ic_fruit_amora, name = "Amora")
                    Spacer(modifier = Modifier.width(10.dp))
                    FruitBadge(drawableId = R.drawable.ic_fruit_manga, name = "Manga")
                    Spacer(modifier = Modifier.width(10.dp))
                    FruitBadge(drawableId = R.drawable.ic_fruit_jabuticaba, name = "Jabuticaba")
                    Spacer(modifier = Modifier.width(10.dp))
                    FruitBadge(drawableId = R.drawable.ic_fruit_caju, name = "Caju")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mais de 70 espécies frutíferas para explorar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = Stone400
                )
            }

            // Bloco Inferior: Ações Ancoradas no Rodapé
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Google Sign-In (Ação principal com 1 toque)
                GoogleSignInButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    isLoading = isSubmittingGoogle,
                    enabled = !isSubmitting && !isSubmittingGoogle,
                    text = "Continuar com o Google",
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Explorar sem conta
                OutlinedButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Stone200),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Stone700
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Stone700
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Explorar o mapa sem conta",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp
                        )
                    )
                }

                // 3. Divisor suave
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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

                // 4. Entrar com e-mail e senha
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Stone200),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Stone700
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Stone700
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Entrar com e-mail",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                }

                // 5. Rodapé: Criar conta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 4.dp)
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
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FruitBadge(
    drawableId: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, Stone200.copy(alpha = 0.8f)),
        modifier = modifier
            .size(44.dp)
            .shadow(
                elevation = 3.dp,
                shape = CircleShape,
                ambientColor = Stone400.copy(alpha = 0.15f),
                spotColor = MamaoOrange.copy(alpha = 0.15f)
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = name,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
