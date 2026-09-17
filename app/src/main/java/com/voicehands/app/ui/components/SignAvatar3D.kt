package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.voicehands.app.lsc.EncuadreAvatarFijo
import com.voicehands.app.lsc.PerfilGesto3d
import com.voicehands.app.lsc.AvatarAssets
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
 * Por defecto carga el avatar base en idle; [assetPath] puede ser un GLB de seña.
 */
@Composable
fun SignAvatar3D(
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    perfil: PerfilGesto3d = perfilGestoParaMotion(motion),
    /** Incrementar al cambiar de token en una oración aunque el [motion] se repita. */
    sceneRevision: Int = 0,
    assetPath: String = AvatarAssets.BASE_GLB,
) {
    val encuadre = EncuadreAvatarFijo
    val engine = rememberEngine()
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = encuadre.camX, y = encuadre.camY, z = encuadre.camZ)
        lookAt(Position(x = encuadre.lookX, y = encuadre.lookY, z = encuadre.lookZ))
    }

    Box(modifier = modifier) {
        key(sceneRevision, assetPath) {
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
                        // Cadera/cintura en el origen: el borde inferior del visor corta a la cintura.
                        centerOrigin = Position(x = 0f, y = encuadre.centerOriginY, z = 0f),
                        position = Position(
                            x = encuadre.modelPosX,
                            y = encuadre.modelPosY,
                            z = encuadre.modelPosZ,
                        ),
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
