package com.voicehands.app.ui.components

import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.voicehands.app.lsc.AvatarAssets

/**
 * Componente visor para el Avatar de Señas 2D (Soporta Videos MP4/WebM, GIFs e Imágenes PNG/WebP).
 *
 * Características principales:
 * - **Soporte de Transparencia (Efecto Sticker)**: Fondo transparente para integrarse sobre temas claros y oscuros.
 * - **Disolución Cruzada Fluida**: Transición entre imágenes utilizando [AnimatedContent] sin saltos posicionales.
 * - **Reproducción de Video**: Integración con AndroidX Media3 (ExoPlayer) para videos MP4/WebM transparentes.
 * - **Badge/Píldora Flotante**: Muestra la letra o seña activa en grande en la esquina superior del visor.
 *
 * @param mediaPath Ruta del recurso multimedia dentro de assets/raw o URL remota.
 * @param modifier Modificador de diseño para Compose.
 * @param motion Movimiento o categoría de la seña.
 * @param letraBadge Letra a destacar en la píldora flotante (ej: "L", "U", "C").
 * @param fondo Color de fondo del visor (por defecto [Color.Transparent]).
 * @param onMediaEnded Callback invocado al finalizar la reproducción de un video.
 */
@OptIn(UnstableApi::class)
@Composable
fun SignAvatarMedia(
    mediaPath: String?,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") motion: AvatarMotion = AvatarMotion.NEUTRAL,
    letraBadge: String? = null,
    fondo: Color = Color.Transparent,
    onMediaEnded: () -> Unit = {},
) {
    val context = LocalContext.current

    // Determina si el recurso solicitado corresponde a un formato de video
    val esVideo = remember(mediaPath) {
        if (mediaPath.isNullOrBlank()) false
        else mediaPath.endsWith(".mp4", ignoreCase = true) ||
            mediaPath.endsWith(".webm", ignoreCase = true) ||
            mediaPath.endsWith(".mkv", ignoreCase = true)
    }

    Box(
        modifier = modifier.background(fondo),
        contentAlignment = Alignment.Center,
    ) {
        val pathAVisualizar = mediaPath.takeIf { !it.isNullOrBlank() } ?: AvatarAssets.BASE_IMAGE

        if (esVideo && !mediaPath.isNullOrBlank()) {
            // Reproductor de Video (AndroidX Media3 ExoPlayer)
            var player: ExoPlayer? by remember { mutableStateOf(null) }

            DisposableEffect(mediaPath) {
                val exoPlayer = ExoPlayer.Builder(context).build().apply {
                    val uri = if (mediaPath.startsWith("http://") || mediaPath.startsWith("https://") || mediaPath.startsWith("content://") || mediaPath.startsWith("file://")) {
                        Uri.parse(mediaPath)
                    } else {
                        Uri.parse("asset:///$mediaPath")
                    }
                    setMediaItem(MediaItem.fromUri(uri))
                    prepare()
                    playWhenReady = true
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                onMediaEnded()
                            }
                        }
                    })
                }
                player = exoPlayer

                onDispose {
                    exoPlayer.release()
                    player = null
                }
            }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }
                },
                update = { view ->
                    view.player = player
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Visor de Imagen con Transición Disuelta entre Poses (Sin saltos ni parpadeo)
            AnimatedContent(
                targetState = pathAVisualizar,
                transitionSpec = {
                    val entrando = fadeIn(animationSpec = tween(280, easing = LinearOutSlowInEasing))
                    val saliendo = fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing))
                    ContentTransform(entrando, saliendo)
                },
                label = "transicion_avatar_movimiento",
            ) { targetPath ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(
                            if (targetPath.startsWith("http") || targetPath.startsWith("content://") || targetPath.startsWith("file://")) {
                                targetPath
                            } else {
                                "file:///android_asset/$targetPath"
                            },
                        )
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(false)
                        .build(),
                    contentDescription = "Avatar de Señas",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        // Píldora/Badge Flotante con la Letra activa sobre el Avatar
        if (!letraBadge.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                tonalElevation = 6.dp,
                shadowElevation = 4.dp,
            ) {
                Text(
                    text = letraBadge.uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}
