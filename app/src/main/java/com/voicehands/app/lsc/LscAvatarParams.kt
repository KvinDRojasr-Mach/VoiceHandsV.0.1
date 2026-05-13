package com.voicehands.app.lsc

import com.voicehands.app.ui.components.AvatarMotion

/**
 * Parámetros visuales por seña para el avatar 3D de demostración.
 * No reproducen la fonética real de la LSC (manos, lugar, orientación); sirven para distinguir
 * señas en la app hasta disponer de clips o modelos validados por la comunidad sorda.
 */
data class PerfilGesto3d(
    val rotX: Float,
    val rotY: Float,
    val rotZ: Float,
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val scaleToUnits: Float,
    val animationSpeed: Float,
    /** Glosa o etiqueta de referencia para subtítulos (orientativo). */
    val glossReferencia: String,
    /** Descripción breve del cuerpo / espacio (orientativo, no certificación LSC). */
    val descripcionCuerpo: String,
)

fun perfilGestoParaMotion(motion: AvatarMotion): PerfilGesto3d = when (motion) {
    AvatarMotion.NEUTRAL -> PerfilGesto3d(
        rotX = 0f, rotY = 0f, rotZ = 0f,
        posX = 0f, posY = -0.55f, posZ = -2.35f,
        scaleToUnits = 1.15f,
        animationSpeed = 0.35f,
        glossReferencia = "REPOSO",
        descripcionCuerpo = "Postura neutra; sin seña activa.",
    )
    AvatarMotion.SALUDO -> PerfilGesto3d(
        rotX = -12f, rotY = 38f, rotZ = 6f,
        posX = 0.12f, posY = -0.42f, posZ = -2.1f,
        scaleToUnits = 1.28f,
        animationSpeed = 1.45f,
        glossReferencia = "HOLA / SALUDO",
        descripcionCuerpo = "Referencia: mano abierta cerca de la sien, movimiento de saludo (lateral).",
    )
    AvatarMotion.AGRADECIMIENTO -> PerfilGesto3d(
        rotX = 8f, rotY = -32f, rotZ = -4f,
        posX = -0.08f, posY = -0.5f, posZ = -2.25f,
        scaleToUnits = 1.05f,
        animationSpeed = 0.55f,
        glossReferencia = "GRACIAS",
        descripcionCuerpo = "Referencia: mano plana al mentón y proyección hacia delante.",
    )
    AvatarMotion.AYUDA -> PerfilGesto3d(
        rotX = -18f, rotY = 5f, rotZ = -10f,
        posX = 0f, posY = -0.35f, posZ = -1.95f,
        scaleToUnits = 1.35f,
        animationSpeed = 1.15f,
        glossReferencia = "AYUDA",
        descripcionCuerpo = "Referencia: manos en ‘A’ o ambas manos altas (variante regional).",
    )
    AvatarMotion.SORDO -> PerfilGesto3d(
        rotX = 4f, rotY = -48f, rotZ = 2f,
        posX = -0.15f, posY = -0.48f, posZ = -2.4f,
        scaleToUnits = 1.12f,
        animationSpeed = 0.65f,
        glossReferencia = "SORDO / AUDICIÓN",
        descripcionCuerpo = "Referencia: índice en mejilla o cerca del oído (según variante docente).",
    )
    AvatarMotion.AGUA -> PerfilGesto3d(
        rotX = 22f, rotY = 15f, rotZ = -6f,
        posX = 0.18f, posY = -0.58f, posZ = -2.2f,
        scaleToUnits = 1.08f,
        animationSpeed = 0.85f,
        glossReferencia = "AGUA",
        descripcionCuerpo = "Referencia: mano en ‘W’ o dedo en comisura labial (según enseñanza local).",
    )
    AvatarMotion.COMIDA -> PerfilGesto3d(
        rotX = 16f, rotY = -8f, rotZ = 12f,
        posX = 0.05f, posY = -0.52f, posZ = -2.15f,
        scaleToUnits = 1.18f,
        animationSpeed = 0.95f,
        glossReferencia = "COMER / COMIDA",
        descripcionCuerpo = "Referencia: punta de dedos hacia la boca (alimentación).",
    )
    AvatarMotion.BANO -> PerfilGesto3d(
        rotX = -6f, rotY = 28f, rotZ = -14f,
        posX = -0.12f, posY = -0.6f, posZ = -2.5f,
        scaleToUnits = 0.98f,
        animationSpeed = 0.5f,
        glossReferencia = "BAÑO / WC",
        descripcionCuerpo = "Referencia: ‘T’ en pecho o letra regional de servicio sanitario.",
    )
    AvatarMotion.SI_GESTO -> PerfilGesto3d(
        rotX = -25f, rotY = -12f, rotZ = 4f,
        posX = 0.22f, posY = -0.4f, posZ = -2.05f,
        scaleToUnits = 1.32f,
        animationSpeed = 1.25f,
        glossReferencia = "SÍ / AFIRMATIVO",
        descripcionCuerpo = "Referencia: pulgar hacia arriba (como convención icónica extendida).",
    )
    AvatarMotion.NO_GESTO -> PerfilGesto3d(
        rotX = 18f, rotY = 42f, rotZ = -8f,
        posX = -0.2f, posY = -0.45f, posZ = -2.3f,
        scaleToUnits = 1.22f,
        animationSpeed = 1.1f,
        glossReferencia = "NO / NEGACIÓN",
        descripcionCuerpo = "Referencia: pulgar hacia abajo o meñique (según contexto).",
    )
    AvatarMotion.POR_FAVOR -> PerfilGesto3d(
        rotX = 2f, rotY = -22f, rotZ = 10f,
        posX = 0f, posY = -0.5f, posZ = -2.28f,
        scaleToUnits = 1.1f,
        animationSpeed = 0.7f,
        glossReferencia = "POR FAVOR",
        descripcionCuerpo = "Referencia: palma hacia el cuerpo, movimiento circular o plano hacia afuera.",
    )
    AvatarMotion.DESCONOCIDO -> PerfilGesto3d(
        rotX = 0f, rotY = 0f, rotZ = 0f,
        posX = 0f, posY = -0.55f, posZ = -2.35f,
        scaleToUnits = 1.0f,
        animationSpeed = 0.25f,
        glossReferencia = "—",
        descripcionCuerpo = "Sin entrada en el diccionario demo; animación neutra.",
    )
}
