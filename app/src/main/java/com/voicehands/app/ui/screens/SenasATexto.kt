package com.voicehands.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicehands.app.ui.theme.CelestePrimary

// PANTALLA: Señas a Texto
// Esta vista es el núcleo del proyecto. Aquí se implementará la captura de video
// en tiempo real y el reconocimiento para traducir las señas a texto u oralidad.
@Composable
fun SenasATextoScreen() {
    // Se utiliza un Column para organizar los elementos verticalmente (cámara arriba, botón abajo).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. CONTENEDOR DE LA CÁMARA
        // Se crea una tarjeta (Card) que servirá como marco para el video en vivo.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Toma el espacio disponible dejando lugar para el botón inferior
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            // Box permite superponer elementos (texto sobre el video, botones flotantes).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray) // Color temporal oscuro mientras se conecta CameraX
            ) {
                // --- AQUÍ IRÁ EL COMPONENTE DE CAMERAX EN EL FUTURO ---
                Text(
                    text = "El video de la cámara aparecerá aquí",
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Center)
                )

                // 2. BARRA SUPERIOR SUPERPUESTA (Estado y Cambio de Cámara)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CAFÉ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Detectando",
                            color = Color(0xFF4CAF50), // Verde
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Punto verde parpadeante (simulado)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        // Icono para cambiar de cámara frontal a trasera
                        Icon(
                            imageVector = Icons.Outlined.Cameraswitch,
                            contentDescription = "Cambiar cámara",
                            tint = Color.White
                        )
                    }
                }

                // 3. ETIQUETA FLOTANTE (Manos detectadas)
                Surface(
                    modifier = Modifier
                        .padding(top = 70.dp, start = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50) // Verde
                ) {
                    Text(
                        text = "● 2 Manos Detectadas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp
                    )
                }

                // 4. TARJETA INFERIOR SUPERPUESTA (Traducción final)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Traducción:",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "La persona está diciendo: Buenos días",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = "Reproducir voz",
                            tint = CelestePrimary
                        )
                    }
                }
            }
        }

        // 5. BOTÓN PRINCIPAL DE ACCIÓN
        // Este botón iniciará o detendrá el flujo de captura y análisis de MediaPipe.
        Button(
            onClick = { /* TODO: Lógica para pedir permisos e iniciar cámara */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = CelestePrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Detección",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Espaciador inferior para que el botón no quede pegado a la barra de navegación
        Spacer(modifier = Modifier.height(8.dp))
    }
}