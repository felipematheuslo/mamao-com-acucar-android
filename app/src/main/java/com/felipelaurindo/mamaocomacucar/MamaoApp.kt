package com.felipelaurindo.mamaocomacucar

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipelaurindo.mamaocomacucar.ui.auth.AuthState
import com.felipelaurindo.mamaocomacucar.ui.auth.AuthViewModel
import com.felipelaurindo.mamaocomacucar.ui.auth.LoginScreen
import com.felipelaurindo.mamaocomacucar.ui.auth.RegisterScreen
import com.felipelaurindo.mamaocomacucar.ui.map.MapScreen

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
        startDestination = "auth/login"
    ) {
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
