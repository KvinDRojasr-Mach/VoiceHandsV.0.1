package com.voicehands.app.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

data class InfoMano(
    val mano: String = "-",
    val letra: String = "-",
    val numero: String = "-",
    val rawLabel: String = "None"
)

class HandAnalyzer(
    private val context: Context,
    private val isFrontCamera: Boolean,
    private val onGesturesDetected: (
        manoIzquierda: InfoMano,
        manoDerecha: InfoMano,
        totalManos: Int,
        landmarks: List<List<NormalizedLandmark>>
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
            .setMinHandDetectionConfidence(0.08f)
            .setMinHandPresenceConfidence(0.08f)
            .setMinTrackingConfidence(0.08f)
            .setResultListener { result: GestureRecognizerResult, _ ->
                val gestures = result.gestures()
                val rawLandmarks = result.landmarks()
                val totalManos = gestures.size

                var infoIzq = InfoMano()
                var infoDer = InfoMano()

                if (totalManos == 1) {
                    val info = procesarGesto(gestures[0])
                    if (rawLandmarks.isNotEmpty() && rawLandmarks[0].isNotEmpty()) {
                        val posX = rawLandmarks[0][0].x()

                        val esIzquierdaEnPantalla = if (isFrontCamera) {
                            posX > 0.5f
                        } else {
                            posX < 0.5f
                        }

                        if (esIzquierdaEnPantalla) {
                            infoIzq = info.copy(mano = "Izquierda")
                        } else {
                            infoDer = info.copy(mano = "Derecha")
                        }
                    }
                } else if (totalManos >= 2) {
                    val info0 = procesarGesto(gestures[0])
                    val info1 = procesarGesto(gestures[1])

                    val x0 = if (rawLandmarks.size > 0 && rawLandmarks[0].isNotEmpty()) rawLandmarks[0][0].x() else 0f
                    val x1 = if (rawLandmarks.size > 1 && rawLandmarks[1].isNotEmpty()) rawLandmarks[1][0].x() else 1f

                    if (isFrontCamera) {
                        if (x0 > x1) {
                            infoIzq = info0.copy(mano = "Izquierda")
                            infoDer = info1.copy(mano = "Derecha")
                        } else {
                            infoIzq = info1.copy(mano = "Izquierda")
                            infoDer = info0.copy(mano = "Derecha")
                        }
                    } else {
                        if (x0 < x1) {
                            infoIzq = info0.copy(mano = "Izquierda")
                            infoDer = info1.copy(mano = "Derecha")
                        } else {
                            infoIzq = info1.copy(mano = "Izquierda")
                            infoDer = info0.copy(mano = "Derecha")
                        }
                    }
                }

                onGesturesDetected(infoIzq, infoDer, totalManos, rawLandmarks)
            }
            .setErrorListener { error -> error.printStackTrace() }
            .build()

        gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
    }

    private fun procesarGesto(gestureList: List<com.google.mediapipe.tasks.components.containers.Category>): InfoMano {
        if (gestureList.isEmpty()) return InfoMano()

        val topGesture = gestureList[0]
        val originalLabel = topGesture.categoryName().trim()
        val cleanedLabel = originalLabel.uppercase()

        val minScore = if (cleanedLabel.contains("P")) 0.05f else 0.08f

        if (topGesture.score() <= minScore) return InfoMano()

        var letraDet = "-"
        var numDet = "-"

        when {
            cleanedLabel == "1" -> {
                letraDet = "I"
                numDet = "1"
            }
            cleanedLabel == "2" -> {
                letraDet = "V"
                numDet = "2"
            }
            cleanedLabel == "0" -> {
                letraDet = "A"
                numDet = "0"
            }
            cleanedLabel == "3" -> {
                numDet = cleanedLabel
            }
            else -> {
                letraDet = cleanedLabel
            }
        }

        return InfoMano(
            mano = "Detectada",
            letra = letraDet,
            numero = numDet,
            rawLabel = "$originalLabel (${(topGesture.score() * 100).toInt()}%)"
        )
    }

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmapBuffer = imageProxy.toBitmap()
            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
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