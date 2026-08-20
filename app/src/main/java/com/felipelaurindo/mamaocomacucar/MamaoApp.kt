package com.felipelaurindo.mamaocomacucar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipelaurindo.mamaocomacucar.ui.auth.AuthState
import com.felipelaurindo.mamaocomacucar.ui.auth.AuthViewModel
import com.felipelaurindo.mamaocomacucar.ui.auth.LoginScreen
import com.felipelaurindo.mamaocomacucar.ui.auth.RegisterScreen
import com.felipelaurindo.mamaocomacucar.ui.map.MapScreen
import com.felipelaurindo.mamaocomacucar.ui.theme.*

@Composable
fun MamaoApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    // Navigate based on auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("map") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate("auth/login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Loading -> { /* Wait */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen()
        }

        composable("auth/login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate("auth/register")
                }
            )
        }

        composable("auth/register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("map") {
            val currentAuth = authState
            if (currentAuth is AuthState.Authenticated) {
                MapScreen(
                    currentUser = currentAuth.user,
                    onLogout = { authViewModel.logout() }
                )
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    // Pulsing animation for the emoji
    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emoji_scale"
    )

    // Fade-in for text
    val textAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "text_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App logo with pulse
            Image(
                painter = painterResource(id = R.drawable.ic_fruit_mamao),
                contentDescription = "Logo do Mamão com Açúcar",
                modifier = Modifier.size((64 * scale).dp)
            )

            // App name
            Column(
                modifier = Modifier.alpha(textAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "mamão com açúcar",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Stone900
                )
                Text(
                    "Mapeamento colaborativo de fruteiras urbanas 🌳",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .alpha(dotAlpha)
                            .background(MamaoOrange, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        }
    }
}
