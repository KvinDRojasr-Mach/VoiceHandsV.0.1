package com.voicehands.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// PANTALLA: Configuración
// En esta vista se gestionarán las autorizaciones del dispositivo (cámara, micrófono)
// y otras preferencias generales de la aplicación.
@Composable
fun ConfigScreen() {
    // Contenedor principal que abarca toda la pantalla.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Texto temporal que posteriormente será reemplazado por los interruptores (Switches) de permisos.
        Text("Aquí va la Configuración de Permisos (Mockup 4)")
    }
}