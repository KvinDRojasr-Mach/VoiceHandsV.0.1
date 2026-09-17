package com.voicehands.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
                Spacer(modifier = Modifier.height(12.dp))

                when (pestaña) {
                    0 -> {
                        AvatarLscPanel(
                            tituloSeña = tituloPalabras,
                            subtitulo = subPalabrasTyped,
                            motion = motionPalabrasTyped,
                            sceneRevision = revisionAvatarPalabras,
                            assetPath = pathPalabrasTyped,
                            modifier = Modifier.padding(bottom = 12.dp),
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
                    1 -> {
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
                            onClick = {
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
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text("Traducir")
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        AvatarLscPanel(
                            tituloSeña = tituloAvatarOracion,
                            subtitulo = subtituloAvatarOracion,
                            motion = motionOracion,
                            sceneRevision = revisionAvatarOracion,
                            assetPath = AvatarAssets.BASE_GLB,
                            modifier = Modifier.weight(1f),
                        )
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
) {
    val borde = if (seleccionada) CelestePrimaryDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
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
        elevation = CardDefaults.cardElevation(defaultElevation = if (seleccionada) 4.dp else 2.dp),
        border = BorderStroke(1.5.dp, borde),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CelestePrimaryLight.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = item.emoji.ifBlank { "🤟" }, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.palabraFrase,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}
