package com.voicehands.app.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.abs
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

/** Estado del avatar compartido entre pestañas/orientación (una sola instancia 3D). */
private data class AvatarSlot(
    val motion: AvatarMotion,
    val revision: Int,
    val path: String,
    val titulo: String,
    val subtitulo: String?,
    val compact: Boolean,
)

/**
 * Resuelve una palabra escrita a la [clave] del diccionario Room (etiqueta o alias).
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

@Composable
fun TextoAsenasScreen(
    consultaBuscador: String,
    onMostrarBuscadorCabecera: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val app = context.applicationContext as VoiceHandsApp
    val repo = app.senasRepository
    val scope = rememberCoroutineScope()
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var pestaña by rememberSaveable { mutableIntStateOf(0) }
    SideEffect { onMostrarBuscadorCabecera(pestaña == 0) }

    val senas by repo.observeSenasFiltradas(consultaBuscador).collectAsState(initial = emptyList())
    val todasActivas by repo.observeSenasActivas().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        VoiceHandsDbSeeder.ensureAvatarBase(app.database)
    }

    var itemSeleccionado by remember { mutableStateOf<DiccionarioSeniaEntity?>(null) }
    var revisionAvatarPalabras by remember { mutableIntStateOf(0) }
    var playbackSeleccionado by remember { mutableStateOf<AnimacionPlayback?>(null) }

    var textoOracion by rememberSaveable { mutableStateOf("") }
    var tituloAvatarOracion by remember { mutableStateOf("Oración a señas") }
    var subtituloAvatarOracion by remember { mutableStateOf<String?>("Escribe texto y pulsa Traducir.") }
    var motionOracion by remember { mutableStateOf(AvatarMotion.NEUTRAL) }
    var jobSecuencia by remember { mutableIntStateOf(0) }
    var secuenciaOracion by remember { mutableStateOf<List<Pair<String, AvatarMotion>>>(emptyList()) }
    var revisionAvatarOracion by remember { mutableIntStateOf(0) }

    LaunchedEffect(itemSeleccionado?.idSenia) {
        val sel = itemSeleccionado
        playbackSeleccionado = if (sel == null) null else repo.getAnimacionParaClave(sel.clave)
    }

    LaunchedEffect(jobSecuencia) {
        if (secuenciaOracion.isEmpty()) return@LaunchedEffect
        for ((palabra, motion) in secuenciaOracion) {
            revisionAvatarOracion++
            tituloAvatarOracion = palabra
            motionOracion = motion
            subtituloAvatarOracion = if (motion == AvatarMotion.DESCONOCIDO) {
                "Sin equivalencia en el diccionario; gesto neutro."
            } else {
                "Entrada del diccionario Room (anim. GLB pendiente o placeholder)."
            }
            delay(1350)
        }
        revisionAvatarOracion++
        tituloAvatarOracion = "Secuencia terminada"
        motionOracion = AvatarMotion.NEUTRAL
        subtituloAvatarOracion = "Puedes editar el texto y pulsar Traducir de nuevo."
    }

    val tituloPalabras: String
    val motionPalabrasTyped: AvatarMotion
    val subPalabrasTyped: String
    val pathPalabrasTyped: String
    when (val sel = itemSeleccionado) {
        null -> {
            tituloPalabras = "Palabra"
            motionPalabrasTyped = AvatarMotion.NEUTRAL
            subPalabrasTyped = "Elige una palabra de la lista. El avatar permanece en espera."
            pathPalabrasTyped = AvatarAssets.BASE_GLB
        }
        else -> {
            val pb = playbackSeleccionado
            val tieneGlb = pb?.tieneGlb == true
            tituloPalabras = sel.palabraFrase
            motionPalabrasTyped = if (tieneGlb) motionParaClave(sel.clave) else AvatarMotion.NEUTRAL
            pathPalabrasTyped = pb?.assetPathOrNull ?: AvatarAssets.BASE_GLB
            subPalabrasTyped = when {
                pb == null -> "Buscando animación en BD…"
                tieneGlb -> "Reproduciendo seña: ${pb.assetPathOrNull}"
                else -> "Sin GLB de seña aún (clave=${sel.clave}). Avatar en espera."
            }
        }
    }

    fun onTraducirClick() {
        scope.launch {
            val tokens = tokenizarOracion(textoOracion)
            if (tokens.isEmpty()) {
                revisionAvatarOracion++
                tituloAvatarOracion = "Sin texto"
                subtituloAvatarOracion = "Escribe al menos una palabra."
                motionOracion = AvatarMotion.NEUTRAL
                secuenciaOracion = emptyList()
            } else {
                secuenciaOracion = tokens.map { token ->
                    val clave = resolverClaveDesdeToken(
                        token = token,
                        senias = todasActivas,
                        getAliases = { id -> repo.getAliases(id) },
                    )
                    val motion = if (clave != null) {
                        motionParaClave(clave)
                    } else {
                        AvatarMotion.DESCONOCIDO
                    }
                    token to motion
                }
                jobSecuencia++
            }
        }
    }

    // Un solo SceneView/GLB que se mueve entre layouts (pestaña / orientación) sin recrearse.
    val avatarMovible = remember {
        movableContentOf { slot: AvatarSlot, modifier: Modifier ->
            AvatarLscPanel(
                tituloSeña = slot.titulo,
                subtitulo = slot.subtitulo,
                motion = slot.motion,
                sceneRevision = slot.revision,
                assetPath = slot.path,
                compact = slot.compact,
                modifier = modifier,
            )
        }
    }

    val slotActivo = AvatarSlot(
        motion = if (pestaña == 0) motionPalabrasTyped else motionOracion,
        revision = if (pestaña == 0) revisionAvatarPalabras else revisionAvatarOracion,
        path = if (pestaña == 0) pathPalabrasTyped else AvatarAssets.BASE_GLB,
        titulo = if (pestaña == 0) tituloPalabras else tituloAvatarOracion,
        subtitulo = if (pestaña == 0) subPalabrasTyped else subtituloAvatarOracion,
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
            // Misma tinta que el skybox del avatar (claro: background; oscuro: surface).
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
                        text = { Text("Palabras") },
                    )
                    Tab(
                        selected = pestaña == 1,
                        onClick = { pestaña = 1 },
                        text = { Text("Oraciones") },
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (landscape) {
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
                                Text(
                                    text = "Palabras más usadas",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    flingBehavior = rememberFlingSuavePastillas(),
                                ) {
                                    items(senas, key = { it.idSenia }) { item ->
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
                                    label = { Text("Escribe la oración") },
                                    placeholder = { Text("Ej.: Hola gracias por favor") },
                                    minLines = 2,
                                    maxLines = 4,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CelestePrimaryLight,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        cursorColor = CelestePrimaryDark,
                                    ),
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onTraducirClick() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                ) {
                                    Text("Traducir")
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        if (pestaña == 1) {
                            OutlinedTextField(
                                value = textoOracion,
                                onValueChange = { textoOracion = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Escribe la oración") },
                                placeholder = { Text("Ej.: Hola gracias por favor") },
                                minLines = 3,
                                maxLines = 6,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CelestePrimaryLight,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    cursorColor = CelestePrimaryDark,
                                ),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onTraducirClick() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text("Traducir")
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            avatarMovible(
                                slotActivo,
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 260.dp, max = 340.dp),
                            )
                        } else {
                            avatarMovible(
                                slotActivo,
                                Modifier.padding(bottom = 8.dp),
                            )
                            Text(
                                text = "Palabras más usadas",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                flingBehavior = rememberFlingSuavePastillas(),
                            ) {
                                items(senas, key = { it.idSenia }) { item ->
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

@Composable
private fun TarjetaSenaDb(
    item: DiccionarioSeniaEntity,
    seleccionada: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    val borde = if (seleccionada) CelestePrimaryDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    val emojiSize = if (compact) 44.dp else 56.dp
    val emojiSp = if (compact) 22.sp else 28.sp
    val padV = if (compact) 10.dp else 16.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) {
                CelestePrimaryLight.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
        // Sin elevación: menos overdraw y scroll más ligero.
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(if (seleccionada) 1.5.dp else 1.dp, borde),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = padV, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(emojiSize)
                    .clip(CircleShape)
                    .background(CelestePrimaryLight.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = item.emoji.ifBlank { "🤟" }, fontSize = emojiSp)
            }
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
            Text(
                text = item.palabraFrase,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Fling con menos fricción: las pastillas “planean” más al soltar el dedo. */
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
