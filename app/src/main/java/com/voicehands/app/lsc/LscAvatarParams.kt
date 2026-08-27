package com.voicehands.app.lsc

import com.voicehands.app.ui.components.AvatarMotion

/**
 * Cámara fija de frente, plano americano (cintura a cabeza).
 * El origen del GLB se asume en los pies; [lookY] apunta al pecho.
 */
object EncuadreAvatarFijo {
    const val camX = 0f
    const val camY = 1.22f
    const val camZ = 0.92f
    const val lookX = 0f
    const val lookY = 1.14f
    const val lookZ = 0f
    const val scaleToUnits = 1.70f
}

/**
 * Metadatos por seña (glosa y velocidad del clip). La pose de cámara es [EncuadreAvatarFijo].
 * No reproducen la fonética real de la LSC hasta disponer de clips validados.
 */
data class PerfilGesto3d(
    val animationSpeed: Float,
    val glossReferencia: String,
    val descripcionCuerpo: String,
)

fun perfilGestoParaMotion(motion: AvatarMotion): PerfilGesto3d = when (motion) {
    AvatarMotion.NEUTRAL -> PerfilGesto3d(
        animationSpeed = 0.35f,
        glossReferencia = "REPOSO",
        descripcionCuerpo = "Postura neutra; sin seña activa.",
    )
    AvatarMotion.SALUDO -> PerfilGesto3d(
        animationSpeed = 1.45f,
        glossReferencia = "HOLA / SALUDO",
        descripcionCuerpo = "Referencia: mano abierta cerca de la sien, movimiento de saludo (lateral).",
    )
    AvatarMotion.AGRADECIMIENTO -> PerfilGesto3d(
        animationSpeed = 0.55f,
        glossReferencia = "GRACIAS",
        descripcionCuerpo = "Referencia: mano plana al mentón y proyección hacia delante.",
    )
    AvatarMotion.AYUDA -> PerfilGesto3d(
        animationSpeed = 1.15f,
        glossReferencia = "AYUDA",
        descripcionCuerpo = "Referencia: manos en ‘A’ o ambas manos altas (variante regional).",
    )
    AvatarMotion.SORDO -> PerfilGesto3d(
        animationSpeed = 0.65f,
        glossReferencia = "SORDO / AUDICIÓN",
        descripcionCuerpo = "Referencia: índice en mejilla o cerca del oído (según variante docente).",
    )
    AvatarMotion.AGUA -> PerfilGesto3d(
        animationSpeed = 0.85f,
        glossReferencia = "AGUA",
        descripcionCuerpo = "Referencia: mano en ‘W’ o dedo en comisura labial (según enseñanza local).",
    )
    AvatarMotion.COMIDA -> PerfilGesto3d(
        animationSpeed = 0.95f,
        glossReferencia = "COMER / COMIDA",
        descripcionCuerpo = "Referencia: punta de dedos hacia la boca (alimentación).",
    )
    AvatarMotion.BANO -> PerfilGesto3d(
        animationSpeed = 0.5f,
        glossReferencia = "BAÑO / WC",
        descripcionCuerpo = "Referencia: ‘T’ en pecho o letra regional de servicio sanitario.",
    )
    AvatarMotion.SI_GESTO -> PerfilGesto3d(
        animationSpeed = 1.25f,
        glossReferencia = "SÍ / AFIRMATIVO",
        descripcionCuerpo = "Referencia: pulgar hacia arriba (como convención icónica extendida).",
    )
    AvatarMotion.NO_GESTO -> PerfilGesto3d(
        animationSpeed = 1.1f,
        glossReferencia = "NO / NEGACIÓN",
        descripcionCuerpo = "Referencia: pulgar hacia abajo o meñique (según contexto).",
    )
    AvatarMotion.POR_FAVOR -> PerfilGesto3d(
        animationSpeed = 0.7f,
        glossReferencia = "POR FAVOR",
        descripcionCuerpo = "Referencia: palma hacia el cuerpo, movimiento circular o plano hacia afuera.",
    )
    AvatarMotion.DESCONOCIDO -> PerfilGesto3d(
        animationSpeed = 0.25f,
        glossReferencia = "—",
        descripcionCuerpo = "Sin entrada en el diccionario demo; animación neutra.",
    )
}
