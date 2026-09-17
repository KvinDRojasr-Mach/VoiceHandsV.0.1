package com.voicehands.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.google.android.filament.ColorGrading
import com.google.android.filament.Skybox
import com.google.android.filament.ToneMapper
import com.voicehands.app.lsc.AvatarAssets
import com.voicehands.app.lsc.EncuadreAvatarFijo
import com.voicehands.app.lsc.PerfilGesto3d
import com.voicehands.app.lsc.perfilGestoParaMotion
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.createEnvironment
import io.github.sceneview.createView
import io.github.sceneview.environment.Environment
import io.github.sceneview.math.Position
import io.github.sceneview.math.colorOf
import io.github.sceneview.math.toLinearSpace
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberView
import io.github.sceneview.safeDestroySkybox

/**
 * Color de hoja detrás del avatar: en claro usa [background] (evita el “cuadro” blanco);
 * en oscuro usa [surface] (el que ya coincidía bien).
 */
@Composable
fun colorHojaAvatar(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.surface.luminance() > 0.5f) scheme.background else scheme.surface
}

/**
 * Personaje 3D con skybox **opaco** del mismo color que la hoja (claro/oscuro).
 * Sin transparencia: Filament + alpha terminaba en negro.
 */
@Composable
fun SignAvatar3D(
    motion: AvatarMotion,
    modifier: Modifier = Modifier,
    perfil: PerfilGesto3d = perfilGestoParaMotion(motion),
    sceneRevision: Int = 0,
    assetPath: String = AvatarAssets.BASE_GLB,
    fondo: Color = colorHojaAvatar(),
) {
    val encuadre = EncuadreAvatarFijo
    val engine = rememberEngine()
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = encuadre.camX, y = encuadre.camY, z = encuadre.camZ)
        lookAt(Position(x = encuadre.lookX, y = encuadre.lookY, z = encuadre.lookZ))
    }
    val view = rememberView(engine) {
        createView(engine).apply {
            colorGrading = ColorGrading.Builder()
                .toneMapper(ToneMapper.Linear())
                .build(engine)
            bloomOptions = bloomOptions.apply { enabled = false }
        }
    }
    val environmentLoader = rememberEnvironmentLoader(engine)
    val fondoArgb = fondo.toArgb()
    val environment = remember(environmentLoader, fondoArgb) {
        val base = createEnvironment(environmentLoader, isOpaque = true)
        base.skybox?.let { engine.safeDestroySkybox(it) }
        Environment(
            indirectLight = base.indirectLight,
            skybox = Skybox.Builder()
                .color(colorOf(fondo).toLinearSpace().toFloatArray())
                .build(engine),
            sphericalHarmonics = base.sphericalHarmonics,
        )
    }
    DisposableEffect(environment) {
        onDispose {
            environmentLoader.destroyEnvironment(environment)
        }
    }

    val modelNodeRef = remember { arrayOfNulls<ModelNode>(1) }
    var modeloListo by remember(assetPath) { mutableStateOf(false) }

    Box(
        modifier = modifier.background(fondo),
    ) {
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            view = view,
            cameraNode = cameraNode,
            cameraManipulator = null,
            environment = environment,
            surfaceType = SurfaceType.Surface,
            isOpaque = true,
        ) {
            val modelInstance = rememberModelInstance(modelLoader, assetPath)
            LaunchedEffect(modelInstance) {
                modeloListo = modelInstance != null
            }
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    autoAnimate = true,
                    animationName = null,
                    animationLoop = true,
                    animationSpeed = perfil.animationSpeed,
                    scaleToUnits = encuadre.scaleToUnits,
                    centerOrigin = Position(x = 0f, y = encuadre.centerOriginY, z = 0f),
                    position = Position(
                        x = encuadre.modelPosX,
                        y = encuadre.modelPosY,
                        z = encuadre.modelPosZ,
                    ),
                    apply = {
                        modelNodeRef[0] = this
                        reiniciarAnimacion(perfil.animationSpeed)
                    },
                )
            }
        }

        if (!modeloListo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fondo),
            )
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

    LaunchedEffect(sceneRevision, motion, assetPath, perfil.animationSpeed, modeloListo) {
        if (modeloListo) {
            modelNodeRef[0]?.reiniciarAnimacion(perfil.animationSpeed)
        }
    }
}

private fun ModelNode.reiniciarAnimacion(speed: Float) {
    val count = animationCount
    if (count <= 0) return
    for (i in 0 until count) {
        playAnimation(i, speed = speed, loop = true)
    }
}
