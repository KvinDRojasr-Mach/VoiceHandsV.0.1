package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicehands.app.lsc.AvatarAssets
import com.voicehands.app.lsc.perfilGestoParaMotion

/**
 * Gestos lógicos de la app (cada uno tiene un perfil 3D en [com.voicehands.app.lsc] hasta tener clips LSC en el GLB).
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
 * Visor 3D sin “tarjeta”: fondo = [colorHojaAvatar] (sticker sobre la hoja).
 */
@Composable
fun AvatarLscPanel(
    @Suppress("UNUSED_PARAMETER") tituloSeña: String,
    @Suppress("UNUSED_PARAMETER") subtitulo: String?,
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    sceneRevision: Int = 0,
    assetPath: String = AvatarAssets.BASE_GLB,
    compact: Boolean = false,
) {
    val perfil = perfilGestoParaMotion(motion)
    val colorHoja = colorHojaAvatar()
    Box(
        modifier = if (compact) {
            modifier
                .fillMaxSize()
                .heightIn(max = 420.dp)
        } else {
            modifier
                .fillMaxWidth()
                .height(300.dp)
        },
        contentAlignment = Alignment.Center,
    ) {
        SignAvatar3D(
            motion = motion,
            modifier = Modifier.fillMaxSize(),
            perfil = perfil,
            sceneRevision = sceneRevision,
            assetPath = assetPath,
            fondo = colorHoja,
        )
    }
}
