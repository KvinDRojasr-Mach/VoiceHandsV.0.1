package com.voicehands.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.Mic
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicehands.app.VoiceHandsApp
import com.voicehands.app.data.db.VoiceHandsDbSeeder
import com.voicehands.app.data.db.entity.DiccionarioSeniaEntity
import com.voicehands.app.data.repository.AnimacionPlayback
import com.voicehands.app.lsc.AvatarAssets
import com.voicehands.app.ui.components.AvatarLscPanel
import com.voicehands.app.ui.components.AvatarMotion
import com.voicehands.app.ui.components.colorHojaAvatar
import com.voicehands.app.ui.theme.CelestePrimaryDark
import com.voicehands.app.ui.theme.CelestePrimaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

// =============================================================================
// 1. MODELOS DE DATOS Y FUNCIONES DE TRADUCCIÓN LSC
// =============================================================================

/**
 * Representa el estado compartido del visor de Avatar (ruta de media, título, badges y postura)
 * para mantener la sincronización entre pestañas y cambios de orientación sin recrear nodos.
 */
private data class AvatarSlot(
    val motion: AvatarMotion,
    val revision: Int,
    val path: String,
    val titulo: String,
    val subtitulo: String?,
    val letraBadge: String?,
    val compact: Boolean,
)

/**
 * Define un paso dentro de una secuencia traducida (una seña completa o una letra en deletreo LSC).
 *
 * @param titulo Texto descriptivo del paso (ej: "LUCAS ➔ Letra 'L'").
 * @param subtitulo Explicación contextual de la fuente del recurso.
 * @param pathMedia Ruta del video o imagen en assets/raw.
 * @param motion Movimiento o categoría de gesto asociada.
 * @param letraBadge Letra a destacar en la píldora flotante del avatar.
 * @param duracionBaseMs Tiempo base en milisegundos que se mostrará este paso.
 */
private data class SecuenciaPaso(
    val titulo: String,
    val subtitulo: String,
    val pathMedia: String,
    val motion: AvatarMotion,
    val letraBadge: String? = null,
    val duracionBaseMs: Long = 950L,
)

/**
 * Verifica si el dispositivo cuenta con conexión activa a internet.
 */
private fun hayConexionInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val net = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(net) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Mapea una clave de seña a su categoría de movimiento predeterminada.
 */
private fun motionParaClave(clave: String): AvatarMotion = when (clave) {
    "hola" -> AvatarMotion.SALUDO
    "gracias" -> AvatarMotion.AGRADECIMIENTO
    "ayuda" -> AvatarMotion.AYUDA
    "soy_sordo" -> AvatarMotion.SORDO
    "agua" -> AvatarMotion.AGUA
    "comida" -> AvatarMotion.COMIDA
    "bano" -> AvatarMotion.BANO
    "si" -> AvatarMotion.SI_GESTO
    "no" -> AvatarMotion.NO_GESTO
    "por_favor" -> AvatarMotion.POR_FAVOR
    else -> AvatarMotion.DESCONOCIDO
}

/**
 * Divide una frase en tokens (palabras individuales o frases compuestas como "por favor").
 */
private fun tokenizarOracion(texto: String): List<String> {
    val partes = texto.split(Regex("[\\s,.;:!?¿¡]+")).filter { it.isNotBlank() }
    if (partes.isEmpty()) return emptyList()
    val fusionados = mutableListOf<String>()
    var i = 0
    while (i < partes.size) {
        val a = partes[i].lowercase()
        val b = partes.getOrNull(i + 1)?.lowercase()
        if (a == "por" && b == "favor") {
            fusionados.add("por favor")
            i += 2
        } else {
            fusionados.add(partes[i])
            i += 1
        }
    }
    return fusionados
}

/**
 * Resuelve un token de texto consultando el diccionario de Room por clave, nombre o alias.
 */
private suspend fun resolverClaveDesdeToken(
    token: String,
    senias: List<DiccionarioSeniaEntity>,
    getAliases: suspend (Long) -> List<String>,
): String? {
    val t = token.lowercase().trim()
    senias.firstOrNull { it.clave.equals(t, ignoreCase = true) }?.let { return it.clave }
    senias.firstOrNull { it.palabraFrase.equals(t, ignoreCase = true) }?.let { return it.clave }
    for (s in senias) {
        val aliases = getAliases(s.idSenia)
        if (aliases.any { it.equals(t, ignoreCase = true) }) return s.clave
    }
    return null
}

// =============================================================================
// 2. PANTALLA PRINCIPAL: TEXTO A SEÑAS (TEXTOASENASSCREEN)
// =============================================================================

/**
 * Pantalla principal del módulo Texto a Señas.
 * Permite explorar señas individuales por catálogo o traducir oraciones completas con deletreo en LSC.
 */
@Composable
fun TextoAsenasScreen(
    consultaBuscadorInicial: String = "",
    onMostrarBuscadorCabecera: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val app = context.applicationContext as VoiceHandsApp
    val repo = app.senasRepository
    val scope = rememberCoroutineScope()
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var pestaña by rememberSaveable { mutableIntStateOf(0) }
    SideEffect { onMostrarBuscadorCabecera(false) }

    var consultaBuscador by rememberSaveable { mutableStateOf(consultaBuscadorInicial) }

    val senas by repo.observeSenasFiltradas(consultaBuscador).collectAsState(initial = emptyList())
    val todasActivas by repo.observeSenasActivas().collectAsState(initial = emptyList())

    val senasLetras = remember(senas) {
        senas.filter { it.tipoContenido == "letra" || it.clave.length == 1 }.sortedBy { it.orden }
    }

    LaunchedEffect(Unit) {
        VoiceHandsDbSeeder.ensureAvatarBase(app.database)
    }

    // Estado para la pestaña "Palabras"
    var itemSeleccionado by remember { mutableStateOf<DiccionarioSeniaEntity?>(null) }
    var revisionAvatarPalabras by remember { mutableIntStateOf(0) }
    var playbackSeleccionado by remember { mutableStateOf<AnimacionPlayback?>(null) }

    // Estado para la pestaña "Deletrear" y Secuenciador
    var textoOracion by rememberSaveable { mutableStateOf("") }
    var tituloAvatarOracion by remember { mutableStateOf("Oración a señas") }
    var subtituloAvatarOracion by remember { mutableStateOf<String?>("Escribe o dicta por voz para deletrear.") }
    var pathMediaOracion by remember { mutableStateOf(AvatarAssets.BASE_IMAGE) }
    var letraBadgeOracion by remember { mutableStateOf<String?>(null) }
    var motionOracion by remember { mutableStateOf(AvatarMotion.NEUTRAL) }
    var jobSecuencia by remember { mutableIntStateOf(0) }
    var secuenciaPasos by remember { mutableStateOf<List<SecuenciaPaso>>(emptyList()) }
    var pasoActualIndex by remember { mutableIntStateOf(-1) }
    var enPausa by remember { mutableStateOf(false) }
    var velocidadMultiplicador by remember { mutableFloatStateOf(1.0f) }
    var revisionAvatarOracion by remember { mutableIntStateOf(0) }

    // Reconocimiento de Voz On-Device NATIVO (SpeechRecognizer)
    var estaEscuchandoVoz by remember { mutableStateOf(false) }

    val nativeSpeechRecognizer = remember(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(nativeSpeechRecognizer) {
        onDispose {
            nativeSpeechRecognizer?.destroy()
        }
    }

    // Permiso en tiempo real para la grabación de audio
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher de Reconocimiento de Voz Fallback para Intent de Google
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    textoOracion = matches[0]
                }
            }
        },
    )

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasAudioPermission = isGranted
        },
    )

    LaunchedEffect(itemSeleccionado?.idSenia) {
        val sel = itemSeleccionado
        if (sel != null) {
            playbackSeleccionado = repo.getAnimacionParaClave(sel.clave)
            // Mantiene la imagen de la letra activa durante 1.8 segundos
            delay(1800L)
            // Regresa automáticamente al estado base (Avatar VoiceHands.PNG)
            itemSeleccionado = null
            playbackSeleccionado = null
        } else {
            playbackSeleccionado = null
        }
    }

    // Ejecutor del secuenciador de oraciones y deletreo LSC
    LaunchedEffect(jobSecuencia) {
        if (secuenciaPasos.isEmpty()) return@LaunchedEffect
        for ((index, paso) in secuenciaPasos.withIndex()) {
            pasoActualIndex = index
            while (enPausa) {
                delay(150)
            }
            revisionAvatarOracion++
            tituloAvatarOracion = paso.titulo
            subtituloAvatarOracion = paso.subtitulo
            pathMediaOracion = paso.pathMedia
            motionOracion = paso.motion
            letraBadgeOracion = paso.letraBadge

            val duracionAjustada = (paso.duracionBaseMs / velocidadMultiplicador).toLong()
            delay(duracionAjustada)
        }
        pasoActualIndex = -1
        revisionAvatarOracion++
        tituloAvatarOracion = "Secuencia terminada"
        subtituloAvatarOracion = "Puedes editar el texto y pulsar Traducir de nuevo."
        pathMediaOracion = AvatarAssets.BASE_IMAGE
        letraBadgeOracion = null
        motionOracion = AvatarMotion.NEUTRAL
    }

    val tituloPalabras: String
    val motionPalabrasTyped: AvatarMotion
    val subPalabrasTyped: String
    val pathPalabrasTyped: String
    val letraBadgePalabras: String?
    when (val sel = itemSeleccionado) {
        null -> {
            tituloPalabras = "Palabra"
            motionPalabrasTyped = AvatarMotion.NEUTRAL
            subPalabrasTyped = "Elige una palabra de la lista. El avatar permanece en reposo."
            pathPalabrasTyped = AvatarAssets.BASE_IMAGE
            letraBadgePalabras = null
        }
        else -> {
            val pb = playbackSeleccionado
            val tieneMedia = pb?.tieneMedia == true
            tituloPalabras = sel.palabraFrase
            motionPalabrasTyped = if (tieneMedia) motionParaClave(sel.clave) else AvatarMotion.NEUTRAL
            pathPalabrasTyped = pb?.assetPathOrNull ?: AvatarAssets.BASE_IMAGE
            letraBadgePalabras = if (sel.tipoContenido == "letra" || sel.clave.length == 1) sel.clave.uppercase() else null
            subPalabrasTyped = when {
                pb == null -> "Buscando video/imagen en BD…"
                tieneMedia -> "Reproduciendo seña: ${pb.assetPathOrNull}"
                else -> "Sin video completo aún. Puedes usar 'Oraciones' para deletrearla en LSC."
            }
        }
    }

    /**
     * Inicia la traducción de la frase ingresada en la pestaña "Deletrear".
     */
    fun onTraducirClick() {
        scope.launch {
            enPausa = false
            val tokens = tokenizarOracion(textoOracion)
            if (tokens.isEmpty()) {
                revisionAvatarOracion++
                tituloAvatarOracion = "Sin texto"
                subtituloAvatarOracion = "Escribe o dicta al menos una palabra."
                motionOracion = AvatarMotion.NEUTRAL
                pathMediaOracion = AvatarAssets.BASE_IMAGE
                letraBadgeOracion = null
                secuenciaPasos = emptyList()
            } else {
                val listaArchivosAbecedario = try {
                    context.assets.list("abecedario")?.toSet() ?: emptySet()
                } catch (_: Exception) {
                    emptySet()
                }

                val pasos = mutableListOf<SecuenciaPaso>()
                for ((index, token) in tokens.withIndex()) {
                    val clave = resolverClaveDesdeToken(
                        token = token,
                        senias = todasActivas,
                        getAliases = { id -> repo.getAliases(id) },
                    )
                    val pb = if (clave != null) repo.getAnimacionParaClave(clave) else null
                    if (pb != null && pb.tieneMedia) {
                        pasos += SecuenciaPaso(
                            titulo = token.replaceFirstChar { it.uppercase() },
                            subtitulo = "Seña completa: ${pb.assetPathOrNull}",
                            pathMedia = pb.assetPathOrNull ?: AvatarAssets.BASE_IMAGE,
                            motion = motionParaClave(clave!!),
                            letraBadge = if (clave.length == 1) clave.uppercase() else null,
                            duracionBaseMs = 1350L,
                        )
                    } else {
                        // Deletreo letra por letra (Dactilología LSC)
                        val letrasClean = token.lowercase().filter { it.isLetter() }
                        if (letrasClean.isNotEmpty()) {
                            for (letra in letrasClean) {
                                val nombreArchivo = "$letra.png"
                                val existeEnAssets = listaArchivosAbecedario.contains(nombreArchivo)
                                val rutaFinal = if (existeEnAssets) {
                                    "abecedario/$nombreArchivo"
                                } else {
                                    AvatarAssets.BASE_IMAGE
                                }
                                pasos += SecuenciaPaso(
                                    titulo = "${token.replaceFirstChar { it.uppercase() }} ➔ Letra '${letra.uppercase()}'",
                                    subtitulo = if (existeEnAssets) "Deletreando en LSC ($nombreArchivo)" else "Letra '${letra.uppercase()}' en preparación",
                                    pathMedia = rutaFinal,
                                    motion = AvatarMotion.SALUDO,
                                    letraBadge = letra.uppercase(),
                                    duracionBaseMs = 950L,
                                )
                            }
                        }
                    }

                    // Pausa neutra / Espacio entre palabras (Dactilología LSC)
                    if (index < tokens.size - 1) {
                        pasos += SecuenciaPaso(
                            titulo = "Espacio (Pausa LSC)",
                            subtitulo = "Pausa neutra entre palabras",
                            pathMedia = AvatarAssets.BASE_IMAGE,
                            motion = AvatarMotion.NEUTRAL,
                            letraBadge = "␣",
                            duracionBaseMs = 1200L,
                        )
                    }
                }
                secuenciaPasos = pasos
                jobSecuencia++
            }
        }
    }

    /**
     * Alterna (inicia o detiene) el reconocimiento de voz nativo en segundo plano sin cuadros de diálogo flotantes.
     * Si no hay internet, fuerza la decodificación local (Offline).
     */
    fun toggleDictadoVoz() {
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        val sr = nativeSpeechRecognizer
        if (sr == null) {
            Toast.makeText(context, "El servicio de voz no está habilitado en los ajustes de Android. Escribe en la caja de texto.", Toast.LENGTH_LONG).show()
            return
        }

        if (estaEscuchandoVoz) {
            sr.stopListening()
            estaEscuchandoVoz = false
        } else {
            val tieneInternet = hayConexionInternet(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CO")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                if (!tieneInternet) {
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }
            }

            sr.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    estaEscuchandoVoz = true
                    Toast.makeText(context, if (tieneInternet) "🎙️ Escuchando... habla ahora" else "🎙️ Escuchando sin internet (Offline)... habla ahora", Toast.LENGTH_SHORT).show()
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    estaEscuchandoVoz = false
                }

                override fun onError(error: Int) {
                    estaEscuchandoVoz = false
                }

                override fun onResults(results: Bundle?) {
                    estaEscuchandoVoz = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        textoOracion = matches[0]
                        onTraducirClick()
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val textoDetectado = matches[0]
                        if (textoDetectado != textoOracion) {
                            textoOracion = textoDetectado
                            onTraducirClick()
                        }
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            sr.startListening(intent)
        }
    }

    val avatarMovible = remember {
        movableContentOf { slot: AvatarSlot, modifier: Modifier ->
            AvatarLscPanel(
                tituloSeña = slot.titulo,
                subtitulo = slot.subtitulo,
                motion = slot.motion,
                sceneRevision = slot.revision,
                assetPath = slot.path,
                letraBadge = slot.letraBadge,
                compact = slot.compact,
                modifier = modifier,
            )
        }
    }

    val slotActivo = AvatarSlot(
        motion = if (pestaña == 0) motionPalabrasTyped else motionOracion,
        revision = if (pestaña == 0) revisionAvatarPalabras else revisionAvatarOracion,
        path = if (pestaña == 0) pathPalabrasTyped else pathMediaOracion,
        titulo = if (pestaña == 0) tituloPalabras else tituloAvatarOracion,
        subtitulo = if (pestaña == 0) subPalabrasTyped else subtituloAvatarOracion,
        letraBadge = if (pestaña == 0) letraBadgePalabras else letraBadgeOracion,
        compact = landscape,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val colorHoja = colorHojaAvatar()
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = colorHoja,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            ) {
                TabRow(selectedTabIndex = pestaña) {
                    Tab(
                        selected = pestaña == 0,
                        onClick = { pestaña = 0 },
                        text = { Text("Abecedario") },
                    )
                    Tab(
                        selected = pestaña == 1,
                        onClick = { pestaña = 1 },
                        text = { Text("Deletrear") },
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (landscape) {
                    // Disposición en Modo Horizontal (Split 50/50)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        avatarMovible(
                            slotActivo,
                            Modifier
                                .weight(0.48f)
                                .fillMaxHeight(),
                        )
                        Column(
                            modifier = Modifier
                                .weight(0.52f)
                                .fillMaxHeight()
                                .then(
                                    if (pestaña == 1) {
                                        Modifier.verticalScroll(rememberScrollState())
                                    } else {
                                        Modifier
                                    },
                                ),
                        ) {
                            if (pestaña == 0) {
                                CampoBuscadorPalabras(
                                    consulta = consultaBuscador,
                                    onConsultaChange = { consultaBuscador = it },
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Catálogo de Letras LSC (A - Z)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 135.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    flingBehavior = rememberFlingSuavePastillas(),
                                ) {
                                    items(senasLetras, key = { it.idSenia }) { item ->
                                        TarjetaSenaDb(
                                            item = item,
                                            seleccionada = itemSeleccionado?.idSenia == item.idSenia,
                                            compact = true,
                                            onClick = {
                                                itemSeleccionado = item
                                                revisionAvatarPalabras++
                                            },
                                        )
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = textoOracion,
                                    onValueChange = { textoOracion = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(if (estaEscuchandoVoz) "🎙️ Escuchando... habla ahora" else "Escribe o dicta para deletrear") },
                                    placeholder = { Text("Ej.: Hola Lucas") },
                                    minLines = 2,
                                    maxLines = 4,
                                    shape = RoundedCornerShape(16.dp),
                                    trailingIcon = {
                                        IconButton(onClick = { toggleDictadoVoz() }) {
                                            Icon(
                                                imageVector = if (estaEscuchandoVoz) Icons.Filled.Mic else Icons.Outlined.Mic,
                                                contentDescription = "Dictar por Voz",
                                                tint = if (estaEscuchandoVoz) Color.Red else CelestePrimaryDark,
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (estaEscuchandoVoz) Color.Red else CelestePrimaryLight,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        cursorColor = CelestePrimaryDark,
                                    ),
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    BotonDictadoVoz(
                                        estaEscuchando = estaEscuchandoVoz,
                                        onClick = { toggleDictadoVoz() },
                                        modifier = Modifier.weight(0.48f),
                                    )

                                    Button(
                                        onClick = { onTraducirClick() },
                                        modifier = Modifier.weight(0.52f),
                                        shape = RoundedCornerShape(14.dp),
                                    ) {
                                        Text("Traducir")
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                PanelControlesOracion(
                                    secuenciaPasos = secuenciaPasos,
                                    pasoActualIndex = pasoActualIndex,
                                    enPausa = enPausa,
                                    velocidad = velocidadMultiplicador,
                                    onTogglePausa = { enPausa = !enPausa },
                                    onReplay = {
                                        enPausa = false
                                        jobSecuencia++
                                    },
                                    onVelocidadChange = { velocidadMultiplicador = it },
                                )
                            }
                        }
                    }
                } else {
                    // Disposición en Modo Vertical (Portrait)
                    if (pestaña == 1) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            avatarMovible(
                                slotActivo,
                                Modifier
                                    .fillMaxWidth()
                                    .height(230.dp),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            PanelControlesOracion(
                                secuenciaPasos = secuenciaPasos,
                                pasoActualIndex = pasoActualIndex,
                                enPausa = enPausa,
                                velocidad = velocidadMultiplicador,
                                onTogglePausa = { enPausa = !enPausa },
                                onReplay = {
                                    enPausa = false
                                    jobSecuencia++
                                },
                                onVelocidadChange = { velocidadMultiplicador = it },
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = textoOracion,
                                onValueChange = { textoOracion = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(if (estaEscuchandoVoz) "🎙️ Escuchando... habla ahora" else "Escribe o dicta para deletrear") },
                                placeholder = { Text("Ej.: Hola Lucas") },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(16.dp),
                                trailingIcon = {
                                    IconButton(onClick = { toggleDictadoVoz() }) {
                                        Icon(
                                            imageVector = if (estaEscuchandoVoz) Icons.Filled.Mic else Icons.Outlined.Mic,
                                            contentDescription = "Dictar por Voz",
                                            tint = if (estaEscuchandoVoz) Color.Red else CelestePrimaryDark,
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (estaEscuchandoVoz) Color.Red else CelestePrimaryLight,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    cursorColor = CelestePrimaryDark,
                                ),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                BotonDictadoVoz(
                                    estaEscuchando = estaEscuchandoVoz,
                                    onClick = { toggleDictadoVoz() },
                                    modifier = Modifier.weight(0.48f),
                                )

                                Button(
                                    onClick = { onTraducirClick() },
                                    modifier = Modifier.weight(0.52f),
                                    shape = RoundedCornerShape(14.dp),
                                ) {
                                    Text("Traducir")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            avatarMovible(
                                slotActivo,
                                Modifier.height(220.dp),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            CampoBuscadorPalabras(
                                consulta = consultaBuscador,
                                onConsultaChange = { consultaBuscador = it },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Catálogo de Letras LSC (A - Z)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 135.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                flingBehavior = rememberFlingSuavePastillas(),
                            ) {
                                items(senasLetras, key = { it.idSenia }) { item ->
                                    TarjetaSenaDb(
                                        item = item,
                                        seleccionada = itemSeleccionado?.idSenia == item.idSenia,
                                        onClick = {
                                            itemSeleccionado = item
                                            revisionAvatarPalabras++
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 3. COMPONENTES DE INTERFAZ Y CONTROLES
// =============================================================================

/**
 * Botón animado de dictado por voz con pulso visual cuando está escuchando activamente.
 */
@Composable
private fun BotonDictadoVoz(
    estaEscuchando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = if (estaEscuchando) {
        val pulse by animateFloatAsState(
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(550, easing = androidx.compose.animation.core.LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulse_mic",
        )
        pulse
    } else 1.0f

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (estaEscuchando) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
            } else Color.Transparent,
            contentColor = if (estaEscuchando) {
                MaterialTheme.colorScheme.onErrorContainer
            } else MaterialTheme.colorScheme.primary,
        ),
    ) {
        Icon(
            imageVector = if (estaEscuchando) Icons.Filled.Mic else Icons.Outlined.Mic,
            contentDescription = if (estaEscuchando) "Detener dictado" else "Dictar por voz",
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (estaEscuchando) "Escuchando..." else "Dictar Voz",
            fontSize = 13.sp,
            fontWeight = if (estaEscuchando) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

/**
 * Campo de búsqueda integrado en la pestaña "Palabras".
 */
@Composable
private fun CampoBuscadorPalabras(
    consulta: String,
    onConsultaChange: (String) -> Unit,
) {
    val colorTextoCampo = MaterialTheme.colorScheme.onSurface
    val colorSecundarioCampo = MaterialTheme.colorScheme.onSurfaceVariant

    OutlinedTextField(
        value = consulta,
        onValueChange = onConsultaChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        placeholder = {
            Text(
                text = "Buscar señas comunes…",
                color = colorSecundarioCampo,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = colorSecundarioCampo,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
            focusedTextColor = colorTextoCampo,
            unfocusedTextColor = colorTextoCampo,
            focusedPlaceholderColor = colorSecundarioCampo,
            unfocusedPlaceholderColor = colorSecundarioCampo,
            cursorColor = CelestePrimaryDark,
            focusedBorderColor = CelestePrimaryLight,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        ),
    )
}

/**
 * Panel de controles de reproducción de oraciones estilo cáscara minimalista (Spotify/Apple Music).
 */
@Composable
private fun PanelControlesOracion(
    secuenciaPasos: List<SecuenciaPaso>,
    pasoActualIndex: Int,
    enPausa: Boolean,
    velocidad: Float,
    onTogglePausa: () -> Unit,
    onReplay: () -> Unit,
    onVelocidadChange: (Float) -> Unit,
) {
    if (secuenciaPasos.isEmpty()) return

    val totalPasos = secuenciaPasos.size
    val progreso = if (pasoActualIndex >= 0 && totalPasos > 0) {
        ((pasoActualIndex + 1).toFloat() / totalPasos.toFloat()).coerceIn(0f, 1f)
    } else 1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        tonalElevation = 3.dp,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Indicador de progreso de la frase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (pasoActualIndex in 0 until totalPasos) {
                        secuenciaPasos[pasoActualIndex].titulo
                    } else "Secuencia terminada",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = if (pasoActualIndex in 0 until totalPasos) "${pasoActualIndex + 1}/$totalPasos" else "$totalPasos/$totalPasos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Barra de progreso fina
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botonera de reproducción (Velocidad, Pausa/Play, Repetir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Selector de velocidad tipo cápsula
                Surface(
                    onClick = {
                        val siguienteVelocidad = when (velocidad) {
                            1.0f -> 1.4f
                            1.4f -> 0.75f
                            else -> 1.0f
                        }
                        onVelocidadChange(siguienteVelocidad)
                    },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.height(36.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = "Velocidad",
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = when (velocidad) {
                                0.75f -> "0.7x"
                                1.4f -> "1.4x"
                                else -> "1.0x"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }

                // Botón principal Play/Pause
                Surface(
                    onClick = onTogglePausa,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = if (enPausa) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                            contentDescription = if (enPausa) "Reanudar" else "Pausar",
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }

                // Botón Repetir
                Surface(
                    onClick = onReplay,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Replay,
                            contentDescription = "Repetir",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual para cada letra en el catálogo del "Abecedario LSC".
 */
@Composable
private fun TarjetaSenaDb(
    item: DiccionarioSeniaEntity,
    seleccionada: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    val borde = if (seleccionada) CelestePrimaryDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    val padV = if (compact) 10.dp else 14.dp
    val letra = item.clave.uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) {
                CelestePrimaryLight.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(if (seleccionada) 2.dp else 1.dp, borde),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = padV, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = CircleShape,
                color = if (seleccionada) CelestePrimaryDark else CelestePrimaryLight.copy(alpha = 0.5f),
                contentColor = if (seleccionada) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(if (compact) 42.dp else 48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letra,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Letra $letra",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Fling suave para las listas y rejillas de tarjetas.
 */
@Composable
private fun rememberFlingSuavePastillas(): FlingBehavior {
    val flingSpec = remember {
        exponentialDecay<Float>(frictionMultiplier = 0.42f)
    }
    return remember(flingSpec) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                if (abs(initialVelocity) < 1f) return initialVelocity
                var lastValue = 0f
                var lastVelocity = initialVelocity
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                ).animateDecay(flingSpec) {
                    val delta = value - lastValue
                    lastValue = value
                    lastVelocity = velocity
                    val consumed = scrollBy(delta)
                    if (abs(delta - consumed) > 0.5f) cancelAnimation()
                }
                return lastVelocity
            }
        }
    }
}
