package com.voicehands.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.voicehands.app.analyzer.HandAnalyzer
import com.voicehands.app.analyzer.InfoMano
import com.voicehands.app.ui.components.HandOverlayView
import java.util.Locale

@Composable
fun SenasATextoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    var cantidadManos by remember { mutableIntStateOf(0) }
    var manoIzquierdaInfo by remember { mutableStateOf(InfoMano()) }
    var manoDerechaInfo by remember { mutableStateOf(InfoMano()) }

    var textFieldState by remember { mutableStateOf(TextFieldValue("")) }
    var ultimaLetraConfirmada by remember { mutableStateOf("") }
    var vozActivaEnTiempoReal by remember { mutableStateOf(false) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    fun hablarFluido(texto: String) {
        if (texto.isBlank() || ttsEngine == null) return
        val textoLimpio = texto.trim()
            .lowercase(Locale("es", "CO"))
            .replace(Regex("\\s+"), " ")
        ttsEngine?.speak(textoLimpio, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun insertarEnCursor(nuevoTexto: String) {
        val currentText = textFieldState.text
        val selection = textFieldState.selection
        val start = selection.start.coerceIn(0, currentText.length)
        val end = selection.end.coerceIn(0, currentText.length)
        val newText = currentText.replaceRange(start, end, nuevoTexto)
        val newCursorPos = start + nuevoTexto.length
        textFieldState = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )
    }

    fun borrarEnCursor() {
        val currentText = textFieldState.text
        val selection = textFieldState.selection
        if (currentText.isEmpty()) return

        if (selection.start != selection.end) {
            val start = selection.start.coerceIn(0, currentText.length)
            val end = selection.end.coerceIn(0, currentText.length)
            val newText = currentText.removeRange(start, end)
            textFieldState = TextFieldValue(
                text = newText,
                selection = TextRange(start)
            )
        } else if (selection.start > 0) {
            val cursor = selection.start.coerceIn(1, currentText.length)
            val newText = currentText.removeRange(cursor - 1, cursor)
            textFieldState = TextFieldValue(
                text = newText,
                selection = TextRange(cursor - 1)
            )
        }
    }

    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale("es", "CO")
            }
        }
        ttsEngine = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    val caracterActual = when {
        manoDerechaInfo.letra != "-" -> manoDerechaInfo.letra
        manoIzquierdaInfo.letra != "-" -> manoIzquierdaInfo.letra
        manoDerechaInfo.numero != "-" -> manoDerechaInfo.numero
        manoIzquierdaInfo.numero != "-" -> manoIzquierdaInfo.numero
        else -> "-"
    }

    LaunchedEffect(caracterActual) {
        if (caracterActual != "-") {
            kotlinx.coroutines.delay(300)
            if (caracterActual != ultimaLetraConfirmada) {
                insertarEnCursor(caracterActual)
                if (vozActivaEnTiempoReal) {
                    hablarFluido(caracterActual)
                }
                ultimaLetraConfirmada = caracterActual
            }
        } else {
            if (textFieldState.text.isNotEmpty() && !textFieldState.text.endsWith(" ")) {
                kotlinx.coroutines.delay(2000)
                if (caracterActual == "-") {
                    insertarEnCursor(" ")
                }
            }
            ultimaLetraConfirmada = ""
        }
    }

    val overlayView = remember { HandOverlayView(context) }
    val isFrontal = (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)

    val analyzer = remember(cameraSelector) {
        manoIzquierdaInfo = InfoMano()
        manoDerechaInfo = InfoMano()

        HandAnalyzer(
            context = context,
            isFrontCamera = isFrontal,
            onGesturesDetected = { izq, der, total, landmarks ->
                cantidadManos = total
                overlayView.setLandmarks(landmarks, isFrontal)

                if (total == 0) {
                    manoIzquierdaInfo = InfoMano()
                    manoDerechaInfo = InfoMano()
                } else {
                    manoIzquierdaInfo = izq
                    manoDerechaInfo = der
                }
            }
        )
    }

    LaunchedEffect(cameraSelector) {
        cameraController.unbind()
        cameraController.cameraSelector = cameraSelector
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            analyzer
        )
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    val colorEsquema = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorEsquema.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                controller = cameraController
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    AndroidView(
                        factory = { overlayView },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Conceder Permiso de Cámara")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (cantidadManos == 0) "Esperando mano..." else "Detectando ($cantidadManos mano/s)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Cameraswitch, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colorEsquema.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👈 Izquierda", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colorEsquema.primary)
                    Text("Letra: ${manoIzquierdaInfo.letra}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(modifier = Modifier.height(24.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👉 Derecha", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colorEsquema.primary)
                    Text("Letra: ${manoDerechaInfo.letra}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(modifier = Modifier.height(24.dp).width(1.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Voz", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = vozActivaEnTiempoReal,
                        onCheckedChange = { vozActivaEnTiempoReal = it }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colorEsquema.surface)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Texto / Frase Acumulada:",
                    fontSize = 10.sp,
                    color = colorEsquema.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))

                OutlinedTextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEsquema.primary,
                        textAlign = TextAlign.Center
                    ),
                    placeholder = {
                        Text(
                            text = "Haz señas para escribir...",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorEsquema.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = colorEsquema.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = colorEsquema.surfaceVariant.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (caracterActual != "-") {
                                insertarEnCursor(caracterActual)
                                if (vozActivaEnTiempoReal) {
                                    hablarFluido(caracterActual)
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorEsquema.secondary)
                    ) {
                        Text("Añadir", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { insertarEnCursor(" ") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Espacio", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { borrarEnCursor() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Backspace,
                            contentDescription = "Borrar posición",
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (textFieldState.text.isNotEmpty()) {
                                hablarFluido(textFieldState.text)
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Voz", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = { textFieldState = TextFieldValue("") }
                    ) {
                        Icon(
                            Icons.Outlined.DeleteSweep,
                            contentDescription = "Borrar todo",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}