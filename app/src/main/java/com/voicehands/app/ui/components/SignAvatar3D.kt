package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.voicehands.app.lsc.EncuadreAvatarFijo
import com.voicehands.app.lsc.PerfilGesto3d
import com.voicehands.app.lsc.perfilGestoParaMotion
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelInstance

/**
 * Personaje 3D (glTF/GLB) con cámara fija de cintura a cabeza.
 * El GLB de ejemplo tiene una sola animación; [sceneRevision] reinicia el clip al cambiar de palabra.
 */
@Composable
fun SignAvatar3D(
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    perfil: PerfilGesto3d = perfilGestoParaMotion(motion),
    /** Incrementar al cambiar de token en una oración aunque el [motion] se repita. */
    sceneRevision: Int = 0,
    assetPath: String = "models/avatar_rigged.glb",
) {
    val encuadre = EncuadreAvatarFijo
    val engine = rememberEngine()
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = encuadre.camX, y = encuadre.camY, z = encuadre.camZ)
        lookAt(Position(x = encuadre.lookX, y = encuadre.lookY, z = encuadre.lookZ))
    }

    Box(modifier = modifier) {
        key(sceneRevision) {
            SceneView(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                cameraNode = cameraNode,
                cameraManipulator = null,
                surfaceType = SurfaceType.TextureSurface,
            ) {
                rememberModelInstance(modelLoader, assetPath)?.let { modelInstance ->
                    ModelNode(
                        modelInstance = modelInstance,
                        autoAnimate = true,
                        animationName = null,
                        animationLoop = true,
                        animationSpeed = perfil.animationSpeed,
                        scaleToUnits = encuadre.scaleToUnits,
                        // Pies en el origen para que la cámara (pecho) recorte de cintura a cabeza.
                        centerOrigin = Position(y = -1f),
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                },
        )
    }
}
