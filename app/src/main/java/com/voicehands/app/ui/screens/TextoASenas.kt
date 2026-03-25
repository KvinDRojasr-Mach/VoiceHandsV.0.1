package com.voicehands.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// PANTALLA: Texto a Señas
// Esta vista se encarga de recibir texto o voz del usuario oyente
// y mostrar su traducción en Lengua de Señas para la persona con discapacidad auditiva.
@Composable
fun TextoASenasScreen() {
    // Box actúa como un contenedor que permite apilar elementos o centrarlos.
    // Modifier.fillMaxSize() hace que el contenedor ocupe toda la pantalla disponible.
    // contentAlignment = Alignment.Center asegura que el contenido quede exactamente en el medio.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Text es el componente básico para mostrar cadenas de caracteres en la interfaz.
        // Este es un mensaje temporal (Dummy) que luego será reemplazado por la caja de texto.
        Text("Aquí va la pantalla de Texto a Señas (Para que la persona sorda entienda)")
    }
}