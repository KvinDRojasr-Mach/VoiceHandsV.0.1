package com.voicehands.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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

@Composable
fun SignAvatar3D(
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    perfil: PerfilGesto3d = perfilGestoParaMotion(motion),
    sceneRevision: Int = 0,
    assetPath: String = AvatarAssets.BASE_GLB,
) {
    val engine = rememberEngine()

    // 1. AJUSTE DE CÁMARA: Subimos la cámara y apuntamos al pecho/rostro
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0.0f, y = 1.0f, z = 2.8f) // y=1.0 sube la cámara, z=2.8 la aleja
        lookAt(Position(x = 0.0f, y = 0.6f, z = 0.0f))    // Apunta al centro del torso
    }

    Box(modifier = modifier) {
        key(sceneRevision, assetPath) {
            SceneView(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                cameraNode = cameraNode,
                cameraManipulator = null, // Mantiene la cámara bloqueada
                surfaceType = SurfaceType.TextureSurface,
            ) {
                rememberModelInstance(modelLoader, assetPath)?.let { modelInstance ->
                    ModelNode(
                        modelInstance = modelInstance,
                        autoAnimate = true,
                        animationName = null,
                        animationLoop = false, // Ejecuta la seña 1 sola vez
                        animationSpeed = perfil.animationSpeed,

                        // 2. AJUSTE DEL MODELO: Forzamos tamaño estándar y lo centramos
                        scaleToUnits = 1.8f, // Forza a que el modelo mida aprox 1.8 metros
                        centerOrigin = Position(x = 0f, y = 0f, z = 0f), // Centra el pivote en el medio del cuerpo
                        position = Position(x = 0f, y = -0.6f, z = 0f),  // Baja el avatar para encuadrar la parte superior
                    )
                }
            }
        }

        // Bloqueo de toques en pantalla
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