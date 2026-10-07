package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.voicehands.app.lsc.AvatarAssets

/**
 * Color de hoja detrás del avatar: en claro usa [MaterialTheme.colorScheme.background];
 * en oscuro usa [MaterialTheme.colorScheme.surface].
 */
@Composable
fun colorHojaAvatar(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.surface.luminance() > 0.5f) scheme.background else scheme.surface
}

/**
 * Categorías de movimiento y gestos lógicos del Avatar LSC.
 */
enum class AvatarMotion {
    NEUTRAL,
    SALUDO,
    AGRADECIMIENTO,
    AYUDA,
    SORDO,
    AGUA,
    COMIDA,
    BANO,
    SI_GESTO,
    NO_GESTO,
    POR_FAVOR,
    DESCONOCIDO,
}

/**
 * Contenedor del Avatar LSC (2D / Video / Imagen).
 *
 * Encapsula la vista del avatar recortado con soporte de dimensiones adaptativas para
 * orientaciones en modo vertical (Portrait) y horizontal (Landscape).
 *
 * @param tituloSeña Título opcional de la seña en reproducción.
 * @param subtitulo Subtítulo o fuente contextual de la seña.
 * @param motion Movimiento o postura gestual asociada.
 * @param modifier Modificador de maquetación en Compose.
 * @param sceneRevision Contador de revisiones para forzar refresco de estado.
 * @param assetPath Ruta del recurso multimedia (video o imagen en assets).
 * @param letraBadge Letra a destacar en la píldora flotante del avatar.
 * @param compact Indica si se debe adaptar para orientación horizontal (Landscape).
 * @param onMediaEnded Callback invocado al completar la reproducción de un video.
 */
@Composable
fun AvatarLscPanel(
    @Suppress("UNUSED_PARAMETER") tituloSeña: String,
    @Suppress("UNUSED_PARAMETER") subtitulo: String?,
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") sceneRevision: Int = 0,
    assetPath: String = AvatarAssets.BASE_IMAGE,
    letraBadge: String? = null,
    compact: Boolean = false,
    onMediaEnded: () -> Unit = {},
) {
    Box(
        modifier = if (compact) {
            modifier
                .fillMaxSize()
                .heightIn(max = 380.dp)
        } else {
            modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 250.dp)
        },
        contentAlignment = Alignment.Center,
    ) {
        SignAvatarMedia(
            mediaPath = assetPath,
            motion = motion,
            letraBadge = letraBadge,
            modifier = Modifier.fillMaxSize(),
            onMediaEnded = onMediaEnded,
        )
    }
}
