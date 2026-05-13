package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.voicehands.app.lsc.PerfilGesto3d
import com.voicehands.app.lsc.perfilGestoParaMotion
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberModelInstance

/**
 * Personaje 3D (glTF/GLB). El archivo de ejemplo tiene **una sola** animación esquelética;
 * [perfil] y [sceneRevision] permiten **diferenciar** señas (pose cámara + velocidad) y **reiniciar**
 * el clip al cambiar de palabra en oraciones.
 *
 * Para LSC fiel a manos y morfología: sustituye el GLB por uno con **un clip por seña** o usa vídeo.
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
    key(motion, sceneRevision, perfil.scaleToUnits, perfil.animationSpeed) {
        SceneView(
            modifier = modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
        ) {
            Node(
                position = Position(
                    x = perfil.posX,
                    y = perfil.posY,
                    z = perfil.posZ,
                ),
                rotation = Rotation(
                    x = perfil.rotX,
                    y = perfil.rotY,
                    z = perfil.rotZ,
                ),
            ) {
                rememberModelInstance(modelLoader, assetPath)?.let { modelInstance ->
                    ModelNode(
                        modelInstance = modelInstance,
                        autoAnimate = true,
                        animationName = null,
                        animationLoop = true,
                        animationSpeed = perfil.animationSpeed,
                        scaleToUnits = perfil.scaleToUnits,
                    )
                }
            }
        }
    }
}
