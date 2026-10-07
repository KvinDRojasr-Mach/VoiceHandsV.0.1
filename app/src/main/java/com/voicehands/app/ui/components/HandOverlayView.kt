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

    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 14f
        isAntiAlias = true
    }

    private val tipPointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = 16f
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val HAND_CONNECTIONS = listOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),       // Pulgar
        Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),       // Índice
        Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),  // Medio
        Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),// Anular
        Pair(13, 17), Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20) // Meñique y Palma
    )

    private val FINGER_TIPS = listOf(4, 8, 12, 16, 20)

    private var isFrontal: Boolean = true

    fun setLandmarks(landmarks: List<List<NormalizedLandmark>>, isFrontal: Boolean = true) {
        this.landmarksList = landmarks
        this.isFrontal = isFrontal
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        for (handLandmarks in landmarksList) {
            // Dibujar esqueleto de conexiones
            for (connection in HAND_CONNECTIONS) {
                val start = handLandmarks[connection.first]
                val end = handLandmarks[connection.second]

                val startX = (if (isFrontal) 1f - start.x() else start.x()) * viewWidth
                val startY = start.y() * viewHeight
                val endX = (if (isFrontal) 1f - end.x() else end.x()) * viewWidth
                val endY = end.y() * viewHeight

                canvas.drawLine(startX, startY, endX, endY, linePaint)
            }

            // Dibujar articulaciones y puntas
            for ((index, landmark) in handLandmarks.withIndex()) {
                val x = (if (isFrontal) 1f - landmark.x() else landmark.x()) * viewWidth
                val y = landmark.y() * viewHeight

                val paintToUse = if (index in FINGER_TIPS) tipPointPaint else pointPaint
                canvas.drawCircle(x, y, 10f, paintToUse)
            }
        }
    }
}