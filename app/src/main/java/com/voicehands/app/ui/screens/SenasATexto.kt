package com.voicehands.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

import com.voicehands.app.analyzer.HandAnalyzer

@Composable
fun SenasATextoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    LaunchedEffect(cameraSelector) {
        cameraController.cameraSelector = cameraSelector
    }

    var cantidadManos by remember { mutableIntStateOf(0) }
    var gestoMano1 by remember { mutableStateOf("") }
    var gestoMano2 by remember { mutableStateOf("") }

    val analyzer = remember {
        HandAnalyzer(
            context = context,
            onGestureDetected = { m1, m2, total ->
                cantidadManos = total
                if (total == 0) {
                    gestoMano1 = ""
                    gestoMano2 = ""
                } else {
                    gestoMano1 = m1
                    gestoMano2 = m2
                }
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

                // BARRA SUPERIOR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (cantidadManos == 0) "--" else gestoMano1,
                        color = Color.White,
                        fontSize = 24.sp,
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
                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cameraswitch,
                                contentDescription = "Cambiar cámara",
                                tint = Color.White
                            )
                        }
                    }
                }

                // CHIP CONTEO
                Surface(
                    modifier = Modifier
                        .padding(top = 75.dp, start = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (cantidadManos > 0) Color(0xFF4CAF50) else Color.Gray,
                ) {
                    Text(
                        text = "● $cantidadManos Mano(s) Detectada(s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                    )
                }

                // TARJETA INFERIOR TRADUCCIÓN
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
                                text = "Traducción detectada:",
                                fontSize = 12.sp,
                                color = colorEsquema.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (cantidadManos) {
                                    0 -> "Realiza una seña frente a la cámara"
                                    1 -> "Seña: $gestoMano1"
                                    else -> "Mano 1: $gestoMano1 | Mano 2: $gestoMano2"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
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