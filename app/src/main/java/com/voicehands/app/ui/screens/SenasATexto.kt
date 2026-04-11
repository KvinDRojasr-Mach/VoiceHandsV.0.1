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

/**
 * Pestaña **Señas a Texto**: vista previa de cámara (CameraX), datos de demostración y botón de permiso.
 *
 * - [LifecycleCameraController]: controla apertura/cierre de cámara ligado al ciclo de vida.
 * - [rememberLauncherForActivityResult]: muestra el diálogo de permiso de Android al pulsar el botón inferior.
 * - Los colores de marcos y textos fuera del vídeo usan [MaterialTheme] para adaptarse al tema claro/oscuro.
 */
@Composable
fun SenasATextoScreen() {
    // LocalContext: acceso al Context de Android (necesario para CameraX y permisos).
    val context = LocalContext.current
    // LocalLifecycleOwner: permite que la cámara se desactive sola al salir de la pantalla o de la app.
    val lifecycleOwner = LocalLifecycleOwner.current
    // remember: el controlador se crea una sola vez mientras viva esta composición (no en cada recomposición).
    val cameraController = remember { LifecycleCameraController(context) }

    // Estado: ¿el usuario ya concedió permiso de cámara?
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }

    // Contrato estándar de Android para pedir un permiso en tiempo de ejecución; el resultado actualiza el estado.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted },
    )

    // Atajo al esquema de color actual (claro u oscuro) para no repetir MaterialTheme.colorScheme.
    val colorEsquema = MaterialTheme.colorScheme

    // Column principal: fondo de pantalla del tema + tarjeta de cámara (con peso) + botón fijo abajo.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorEsquema.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Card exterior: marco redondeado alrededor del área de vídeo (color surfaceVariant del tema).
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colorEsquema.surfaceVariant),
        ) {
            // Box apilando: vídeo o mensaje, overlays de UI (barra superior simulada, chip, tarjeta traducción).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Gris oscuro fijo detrás del vídeo: mejora percepción cuando no hay imagen aún.
                    .background(Color(0xFF2C2C2C)),
            ) {
                if (hasCameraPermission) {
                    // AndroidView: incrusta una vista tradicional (PreviewView) dentro de Compose.
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                controller = cameraController
                                cameraController.bindToLifecycle(lifecycleOwner)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    // Sin permiso no hay preview: mostramos mensaje centrado con color legible del tema.
                    Text(
                        text = "La cámara está desactivada",
                        color = colorEsquema.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                // Fila superior simulada (mock): palabra detectada + estado "Detectando" (lectura sobre vídeo).
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "CAFÉ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Detectando",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Outlined.Cameraswitch,
                            contentDescription = "Cambiar cámara",
                            tint = Color.White,
                        )
                    }
                }

                // Chip verde de ejemplo ("manos detectadas"); color fijo para simular estado de ML.
                Surface(
                    modifier = Modifier
                        .padding(top = 70.dp, start = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50),
                ) {
                    Text(
                        text = "● 2 Manos Detectadas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                    )
                }

                // Tarjeta inferior: texto de traducción con colores surface/onSurface del tema (modo claro/oscuro).
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
                                text = "Traducción:",
                                fontSize = 12.sp,
                                color = colorEsquema.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "La persona está diciendo: CAFÉ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorEsquema.onSurface,
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = "Reproducir voz",
                            tint = colorEsquema.primary,
                        )
                    }
                }
            }
        }

        // Botón inferior: dispara la petición de permiso de cámara (o indica que la detección está activa).
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
