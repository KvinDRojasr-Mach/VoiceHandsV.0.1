package com.voicehands.app.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

data class InfoMano(
    val mano: String = "-",        // "Izquierda" o "Derecha"
    val letra: String = "-",       // Cualquier letra A-Z
    val numero: String = "-",      // "0", "1", "2", "3"
    val rawLabel: String = "None" // Etiqueta cruda para depuración
)

class HandAnalyzer(
    private val context: Context,
    private val onGesturesDetected: (
        manoIzquierda: InfoMano,
        manoDerecha: InfoMano,
        totalManos: Int
    ) -> Unit
) : ImageAnalysis.Analyzer {

    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    private fun setupGestureRecognizer() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("gesture_recognizer.task")
            .build()

        val options = GestureRecognizer.GestureRecognizerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(2)
            .setMinHandDetectionConfidence(0.15f)
            .setMinHandPresenceConfidence(0.15f)
            .setMinTrackingConfidence(0.15f)
            .setResultListener { result: GestureRecognizerResult, _ ->
                val gestures = result.gestures()
                val handednessList = result.handedness()
                val totalManos = gestures.size

                var infoIzq = InfoMano()
                var infoDer = InfoMano()

                for (i in 0 until totalManos) {
                    if (gestures[i].isNotEmpty()) {
                        val topGesture = gestures[i][0]

                        if (topGesture.score() > 0.15f) {
                            val originalLabel = topGesture.categoryName().trim()
                            val cleanedLabel = originalLabel.uppercase()

                            var ladoMano = "Desconocida"
                            if (handednessList.size > i && handednessList[i].isNotEmpty()) {
                                val handCategory = handednessList[i][0].categoryName()
                                ladoMano = when (handCategory.lowercase()) {
                                    "left" -> "Derecha"
                                    "right" -> "Izquierda"
                                    else -> handCategory
                                }
                            }

                            var letraDet = "-"
                            var numDet = "-"

                            when {
                                // Mapeo de Números (mantiene desambiguación A/0 si el modelo confunde el puño)
                                cleanedLabel in listOf("0", "1", "2", "3") -> {
                                    numDet = cleanedLabel
                                    if (cleanedLabel == "0") letraDet = "A"
                                }
                                // Mapeo General para todo el Abecedario A-Z
                                cleanedLabel.length == 1 && cleanedLabel[0].isLetter() -> {
                                    letraDet = cleanedLabel
                                }
                                // Soporte para nombres formateados de clases (ej. "VOCAL_E" o "LETTER_B")
                                cleanedLabel.contains("_") -> {
                                    val parteLetra = cleanedLabel.split("_").last()
                                    if (parteLetra.length == 1 && parteLetra[0].isLetter()) {
                                        letraDet = parteLetra
                                    }
                                }
                            }

                            val infoProcesada = InfoMano(
                                mano = ladoMano,
                                letra = letraDet,
                                numero = numDet,
                                rawLabel = "$originalLabel (${(topGesture.score() * 100).toInt()}%)"
                            )

                            if (ladoMano == "Izquierda") {
                                infoIzq = infoProcesada
                            } else if (ladoMano == "Derecha") {
                                infoDer = infoProcesada
                            }
                        }
                    }
                }

                onGesturesDetected(infoIzq, infoDer, totalManos)
            }
            .setErrorListener { error ->
                error.printStackTrace()
            }
            .build()

        gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
    }

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmapBuffer = imageProxy.toBitmap()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                postScale(-1f, 1f, bitmapBuffer.width / 2f, bitmapBuffer.height / 2f)
            }

            val processedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
            )

            val mpImage = BitmapImageBuilder(processedBitmap).build()
            val timestampMs = imageProxy.imageInfo.timestamp / 1_000_000

            gestureRecognizer?.recognizeAsync(mpImage, timestampMs)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }
}