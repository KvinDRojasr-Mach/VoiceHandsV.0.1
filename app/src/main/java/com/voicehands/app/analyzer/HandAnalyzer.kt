package com.voicehands.app.analyzer

import kotlin.math.abs
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

// CLASE: HandAnalyzer
// Esta clase actúa como un puente entre la cámara del dispositivo (CameraX) y el motor de IA (MediaPipe).
// Intercepta los fotogramas de video en tiempo real, los procesa y los envía al modelo para detectar manos.
class HandAnalyzer(
    private val context: Context,
    // Callback: Función que se ejecuta cada vez que MediaPipe encuentra exitosamente una o más manos en el video.
    private val onHandResults: (HandLandmarkerResult) -> Unit,
    private val onNumberDetected: (Int) -> Unit
) : ImageAnalysis.Analyzer {

    // Variable que contendrá el motor de detección inicializado.
    private var handLandmarker: HandLandmarker? = null

    // Bloque de inicialización: Se configura y arranca el modelo en el momento en que se instancia esta clase.
    init {
        setupHandLandmarker()
    }

    // FUNCIÓN: Configurar el detector
    // Carga el archivo pre-entrenado .task desde la carpeta assets y establece los parámetros de visión artificial.
    private fun setupHandLandmarker() {
        // Se apunta al archivo del modelo matemático alojado en la carpeta assets del proyecto.
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()

        // Se configura el detector para que funcione en modo de transmisión en vivo (LIVE_STREAM).
        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(2) // REGLA DE NEGOCIO: Se configura para detectar y rastrear hasta 2 manos simultáneamente.
            .setResultListener { result, _ ->
                // Cuando MediaPipe procesa un fotograma y encuentra coordenadas, se emiten al exterior mediante el callback original.
                onHandResults(result)

                // NUEVA LÓGICA: Extraer puntos y detectar el número en tiempo real
                val landmarks = result.landmarks()
                if (landmarks.isNotEmpty()) {
                    val firstHand = landmarks[0] // Tomamos la primera mano detectada
                    val numero = detectSignLanguageNumber(firstHand)

                    // Si reconoció un número válido (0 al 5), lo enviamos a la interfaz a través del nuevo callback
                    if (numero != -1) {
                        onNumberDetected(numero)
                    }
                }
            }
            .setErrorListener { error ->
                // Si ocurre un error durante la inferencia matemática, se imprime en consola para depuración.
                error.printStackTrace()
            }
            .build()

        // Se crea la instancia final del detector utilizando el contexto de la aplicación y las opciones definidas.
        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }

    // FUNCIÓN: Analizar fotograma
    // Este método es invocado automáticamente por CameraX cada vez que el sensor captura una nueva imagen a 30 o 60 FPS.
    override fun analyze(imageProxy: ImageProxy) {
        try {
            // 1. Extracción: Se convierte el fotograma crudo (ImageProxy) a un formato de mapa de bits estándar (Bitmap).
            val bitmapBuffer = imageProxy.toBitmap()

            // 2. Rotación: Los sensores de los dispositivos móviles entregan imágenes rotadas.
            // Se obtiene el ángulo de rotación exacto proporcionado por la cámara y se aplica al Bitmap.
            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            }
            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
            )

            // 3. Conversión al estándar de IA: MediaPipe requiere un objeto específico llamado MPImage.
            // Se construye el MPImage a partir del Bitmap ya rotado y corregido.
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            // 4. Procesamiento Asíncrono: Se envía la imagen empaquetada a la IA.
            // En modo LIVE_STREAM, es obligatorio proveer la marca de tiempo exacta (timestamp) en milisegundos.
            val timestampMs = imageProxy.imageInfo.timestamp / 1_000_000
            handLandmarker?.detectAsync(mpImage, timestampMs)

        } catch (e: Exception) {
            // Captura de seguridad en caso de que el búfer de imagen venga corrupto o nulo.
            e.printStackTrace()
        } finally {
            // 5. Liberación de memoria: Es obligatorio cerrar el proxy al finalizar la ejecución del bloque.
            // Si el proxy no se cierra, la cámara agota su límite de imágenes y se congela permanentemente.
            imageProxy.close()
        }
    }
}

// LÓGICA DE DETECCIÓN: Compara la altura de las puntas de los dedos con sus respectivos nudillos.
fun detectSignLanguageNumber(landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>): Int {
    val isIndexUp = landmarks[8].y() < landmarks[6].y()
    val isMiddleUp = landmarks[12].y() < landmarks[10].y()
    val isRingUp = landmarks[16].y() < landmarks[14].y()
    val isPinkyUp = landmarks[20].y() < landmarks[18].y()

    val isThumbOpen = abs(landmarks[4].x() - landmarks[9].x()) > abs(landmarks[3].x() - landmarks[9].x())

    return when {
        !isThumbOpen && !isIndexUp && !isMiddleUp && !isRingUp && !isPinkyUp -> 0
        !isThumbOpen && isIndexUp && !isMiddleUp && !isRingUp && !isPinkyUp -> 1
        !isThumbOpen && isIndexUp && isMiddleUp && !isRingUp && !isPinkyUp -> 2
        !isThumbOpen && isIndexUp && isMiddleUp && isRingUp && !isPinkyUp -> 3
        !isThumbOpen && isIndexUp && isMiddleUp && isRingUp && isPinkyUp -> 4
        isThumbOpen && isIndexUp && isMiddleUp && isRingUp && isPinkyUp -> 5
        else -> -1
    }
}