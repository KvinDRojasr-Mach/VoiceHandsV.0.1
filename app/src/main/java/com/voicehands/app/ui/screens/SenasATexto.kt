package com.voicehands.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// PANTALLA: Señas a Texto
// Esta vista es el núcleo del proyecto. Aquí se implementará la captura de video
// en tiempo real y el reconocimiento para traducir las señas a texto u oralidad.
@Composable
fun SenasATextoScreen() {
    // Se utiliza un Box para ocupar toda la pantalla.
    // Más adelante, en este espacio se incrustará el visor de la cámara (CameraX)
    // y la capa de dibujo para los puntos de MediaPipe.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Mensaje temporal para indicar el propósito de la pantalla.
        Text("Aquí va la cámara de Señas a Texto/Voz (Para que el oyente entienda)")
    }
}