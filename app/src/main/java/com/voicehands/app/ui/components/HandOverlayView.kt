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
    var isFrontCamera: Boolean = true

    private val pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = 14f
        isAntiAlias = true
    }

    private val connectionPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val handConnections = listOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),     // Pulgar
        Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),     // Índice
        Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12), // Medio
        Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16), // Anular
        Pair(13, 17), Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20) // Meñique
    )

    fun setLandmarks(landmarks: List<List<NormalizedLandmark>>, isFront: Boolean = true) {
        this.landmarksList = landmarks
        this.isFrontCamera = isFront
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w == 0f || h == 0f) return

        for (handLandmarks in landmarksList) {
            if (handLandmarks.isEmpty()) continue

            // 1. DIBUJAR LÍNEAS DE CONEXIÓN
            for (connection in handConnections) {
                val start = handLandmarks[connection.first]
                val end = handLandmarks[connection.second]

                val startX = if (isFrontCamera) w * (1f - start.x()) else w * start.x()
                val startY = h * start.y()

                val endX = if (isFrontCamera) w * (1f - end.x()) else w * end.x()
                val endY = h * end.y()

                canvas.drawLine(startX, startY, endX, endY, connectionPaint)
            }

            // 2. DIBUJAR PUNTOS DE LA MANO
            for (landmark in handLandmarks) {
                val px = if (isFrontCamera) w * (1f - landmark.x()) else w * landmark.x()
                val py = h * landmark.y()

                canvas.drawCircle(px, py, 10f, pointPaint)
            }
        }
    }
}