package com.felipelaurindo.mamaocomacucar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MamaoOrange,
    onPrimary = Color.White,
    primaryContainer = MamaoOrangeContainer,
    onPrimaryContainer = Stone900,
    secondary = Stone700,
    onSecondary = Color.White,
    secondaryContainer = Stone100,
    onSecondaryContainer = Stone900,
    tertiary = StatusFlowering,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Stone900,
    surface = SurfaceLight,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone600,
    outline = Stone300,
    outlineVariant = Stone200,
    error = Rose600,
    onError = Color.White,
    errorContainer = Rose50,
    onErrorContainer = Rose700
)

private val DarkColorScheme = darkColorScheme(
    primary = MamaoOrange,
    onPrimary = Color.White,
    primaryContainer = MamaoOrangeDark,
    onPrimaryContainer = MamaoOrangeLight,
    secondary = Stone400,
    onSecondary = Stone900,
    secondaryContainer = Stone800,
    onSecondaryContainer = Stone200,
    tertiary = StatusFlowering,
    onTertiary = Color.White,
    background = Stone950,
    onBackground = Stone100,
    surface = Stone900,
    onSurface = Stone100,
    surfaceVariant = Stone800,
    onSurfaceVariant = Stone400,
    outline = Stone600,
    outlineVariant = Stone700,
    error = Rose600,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Rose200
)

@Composable
fun MamaoComAcucarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}