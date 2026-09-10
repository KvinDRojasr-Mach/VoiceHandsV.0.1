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

class HandAnalyzer(
    private val context: Context,
    private val onGestureDetected: (gestoMano1: String, gestoMano2: String, totalManos: Int) -> Unit
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
            // AJUSTE CLAVE 1: Bajar umbrales de confianza para mejorar la precisión de captura
            .setMinHandDetectionConfidence(0.3f)
            .setMinHandPresenceConfidence(0.3f)
            .setMinTrackingConfidence(0.3f)
            .setResultListener { result: GestureRecognizerResult, _ ->
                val gestures = result.gestures()
                var mano1Gesto = "Desconocido"
                var mano2Gesto = "Desconocido"
                val totalManos = gestures.size

                if (gestures.isNotEmpty() && gestures[0].isNotEmpty()) {
                    // Si el score es mayor al 40% se toma como válido
                    val topGesture = gestures[0][0]
                    if (topGesture.score() > 0.4f) {
                        mano1Gesto = topGesture.categoryName()
                    }
                }

                if (gestures.size > 1 && gestures[1].isNotEmpty()) {
                    val topGesture = gestures[1][0]
                    if (topGesture.score() > 0.4f) {
                        mano2Gesto = topGesture.categoryName()
                    }
                }

                onGestureDetected(mano1Gesto, mano2Gesto, totalManos)
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

            // AJUSTE CLAVE 2: Rotar y aplicar reflejo si es necesario para sincronizar ejes
            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                // Si la rotación es de cámara frontal (usualmente 270 o 90 grados), invertimos en X
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