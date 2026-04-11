package com.voicehands.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pestaña **Configuración**: por ahora solo un texto centrado de marcador de posición.
 *
 * - [MaterialTheme.colorScheme.background]: rellena toda el área bajo la cabecera con el fondo del tema.
 * - [onBackground]: color de texto pensado para leerse sobre ese fondo (claro u oscuro).
 * Cuando implementes switches de permisos, sustituye el [Text] por una [Column] con tus controles.
 */
@Composable
fun ConfigScreen() {
    // Box: contenedor simple; fillMaxSize + background pinta toda la zona de contenido de la pestaña.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        // Centra el texto tanto en horizontal como en vertical.
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Aquí va la Configuración de Permisos (Mockup 4)",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
}
