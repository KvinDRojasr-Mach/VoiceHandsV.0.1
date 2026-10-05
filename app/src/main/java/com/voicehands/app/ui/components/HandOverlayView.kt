package com.voicehands.app.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class HandOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var landmarksList: List<List<NormalizedLandmark>> = emptyList()
    var isFrontCamera: Boolean = true // Controla si reflejamos X o no

    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 12f
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val HAND_CONNECTIONS = listOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),       // Pulgar
        Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),       // Índice
        Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),  // Medio
        Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),// Anular
        Pair(13, 17), Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20) // Meñique y Palma
    )

    fun setLandmarks(landmarks: List<List<NormalizedLandmark>>, isFront: Boolean) {
        this.landmarksList = landmarks
        this.isFrontCamera = isFront
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        for (handLandmarks in landmarksList) {
            // Dibujar líneas
            for (connection in HAND_CONNECTIONS) {
                val start = handLandmarks[connection.first]
                val end = handLandmarks[connection.second]

                val startX = if (isFrontCamera) (1f - start.x()) * viewWidth else start.x() * viewWidth
                val startY = start.y() * viewHeight
                val endX = if (isFrontCamera) (1f - end.x()) * viewWidth else end.x() * viewWidth
                val endY = end.y() * viewHeight

                canvas.drawLine(startX, startY, endX, endY, linePaint)
            }

            // Dibujar puntos
            for (landmark in handLandmarks) {
                val x = if (isFrontCamera) (1f - landmark.x()) * viewWidth else landmark.x() * viewWidth
                val y = landmark.y() * viewHeight
                canvas.drawCircle(x, y, 9f, pointPaint)
            }
        }
    }
}