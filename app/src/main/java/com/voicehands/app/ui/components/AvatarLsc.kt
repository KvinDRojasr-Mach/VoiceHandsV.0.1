package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
 * Recuadro con avatar 3D y leyenda (palabra u oración en curso).
 */
@Composable
fun AvatarLscPanel(
    tituloSeña: String,
    subtitulo: String?,
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    /** Cambia al avanzar palabra en oración o al pulsar otra tarjeta para reiniciar el clip 3D. */
    sceneRevision: Int = 0,
) {
    val perfil = perfilGestoParaMotion(motion)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = tituloSeña,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            if (subtitulo != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${perfil.glossReferencia} — ${perfil.descripcionCuerpo}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                SignAvatar3D(
                    motion = motion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    perfil = perfil,
                    sceneRevision = sceneRevision,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Animación genérica en un solo GLB; la LSC real requiere clips o vídeo por seña validados. Glosas arriba son orientativas.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
