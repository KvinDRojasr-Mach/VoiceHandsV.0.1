package com.voicehands.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Esquema de color **claro** (Material 3).
 *
 * Roles usados en la app:
 * - **primary / onPrimary**: azul de marca y contenido encima (cabecera, barra inferior, botones).
 * - **background / onBackground**: color de fondo general de pantallas y texto principal sobre él.
 * - **surface / onSurface**: tarjetas y contenido principal sobre superficie.
 * - **surfaceVariant / onSurfaceVariant**: tarjetas secundarias (p. ej. rejilla de señas) y texto secundario.
 * - **outline**: bordes sutiles (p. ej. borde del buscador).
 */
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
    onSurface = TextoTituloSeccionOscuro,
    surfaceVariant = SenasComunTarjetaFondo,
    onSurfaceVariant = TextoSecundarioContraste,
    outline = Color(0xFFE1E8EE),
)

/**
 * Esquema de color **oscuro** (Material 3).
 *
 * Mantiene el mismo **primary** celeste para identidad visual; fondos y superficies son tonos azulados oscuros
 * para reducir fatiga visual. **onPrimary** sigue siendo blanco para leer bien sobre la franja azul.
 */
private val DarkColors = darkColorScheme(
    primary = CelestePrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = CelestePrimaryDark,
    onPrimaryContainer = CelestePrimaryLight,
    secondary = CelestePrimaryLight,
    onSecondary = CelestePrimaryDark,
    background = BackgroundDark,
    onBackground = TextOnPrimary,
    surface = SurfaceDark,
    onSurface = Color(0xFFE8F4FC),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB0C9DC),
    outline = Color(0xFF3D5A73),
)

/**
 * Tema raíz de la aplicación: aplica tipografía y [ColorScheme] a todos los composables hijos.
 *
 * @param darkTheme Si es true se usa [DarkColors]; si false, [LightColors]. Por defecto sigue al sistema
 * (solo aplica si no pasas el parámetro desde MainActivity).
 * @param content Bloque de UI que recibirá [MaterialTheme.colorScheme] y [MaterialTheme.typography].
 */
@Composable
fun VoiceHandsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VoiceHandsTypography,
        content = content,
    )
}
