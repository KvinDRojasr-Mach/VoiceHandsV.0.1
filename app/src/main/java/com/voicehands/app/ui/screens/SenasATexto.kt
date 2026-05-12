package com.voicehands.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

// IMPORTA TU ANALIZADOR AQUÍ (Asegúrate de que la ruta coincida con tu paquete)
import com.voicehands.app.analyzer.HandAnalyzer

@Composable
fun SenasATextoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    // 1. ESTADOS PARA LA INTELIGENCIA ARTIFICIAL
    var numeroDetectado by remember { mutableStateOf("") }
    var cantidadManos by remember { mutableStateOf(0) }

    // 2. INICIALIZAR EL ANALIZADOR
    val analyzer = remember {
        HandAnalyzer(
            context = context,
            onHandResults = { result ->
                // Actualizamos cuántas manos hay en pantalla
                cantidadManos = result.landmarks().size
                // Si no hay manos, limpiamos el número
                if (cantidadManos == 0) {
                    numeroDetectado = ""
                }
            },
            onNumberDetected = { numero ->
                // Actualizamos el número detectado en tiempo real
                numeroDetectado = numero.toString()
            }
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted },
    )

    val colorEsquema = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorEsquema.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colorEsquema.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C2C2C)),
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                controller = cameraController
                                // 3. CONECTAR LA CÁMARA CON EL ANALIZADOR DE IA
                                cameraController.setImageAnalysisAnalyzer(
                                    ContextCompat.getMainExecutor(ctx),
                                    analyzer
                                )
                                cameraController.bindToLifecycle(lifecycleOwner)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = "La cámara está desactivada",
                        color = colorEsquema.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 4. MOSTRAR EL NÚMERO REAL
                    Text(
                        text = if (numeroDetectado.isEmpty()) "--" else numeroDetectado,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (cantidadManos > 0) "Detectando" else "Buscando manos...",
                            color = if (cantidadManos > 0) Color(0xFF4CAF50) else Color.Yellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (cantidadManos > 0) Color(0xFF4CAF50) else Color.Yellow),
                        )
                    }
                }

                // CHIP DE MANOS REAL
                Surface(
                    modifier = Modifier
                        .padding(top = 70.dp, start = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (cantidadManos > 0) Color(0xFF4CAF50) else Color.Gray,
                ) {
                    Text(
                        text = "● $cantidadManos Manos Detectadas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorEsquema.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Traducción en vivo:",
                                fontSize = 12.sp,
                                color = colorEsquema.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // TEXTO DE TRADUCCIÓN REAL
                            Text(
                                text = if (numeroDetectado.isEmpty()) "Haz una seña con tu mano" else "Número detectado: $numeroDetectado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorEsquema.onSurface,
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorEsquema.surface,
                contentColor = colorEsquema.primary,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasCameraPermission) "Detección Activa" else "Iniciar Detección",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}