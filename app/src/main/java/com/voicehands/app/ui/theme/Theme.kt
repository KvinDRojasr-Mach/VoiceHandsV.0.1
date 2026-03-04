package com.voicehands.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CelestePrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = CelestePrimaryLight,
    onPrimaryContainer = CelestePrimaryDark,
    secondary = CelestePrimaryDark,
    onSecondary = TextOnPrimary,
    background = BackgroundLight,
    onBackground = CelestePrimaryDark,
    surface = SurfaceLight,
    onSurface = CelestePrimaryDark
)

@Composable
fun VoiceHandsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // De momento sólo paleta clara minimalista
    val colorScheme = LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VoiceHandsTypography,
        content = content
    )
}

