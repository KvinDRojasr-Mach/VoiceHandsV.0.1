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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicehands.app.ui.components.AvatarLscPanel
import com.voicehands.app.ui.components.AvatarMotion
import com.voicehands.app.ui.theme.CelestePrimaryDark
import com.voicehands.app.ui.theme.CelestePrimaryLight
import kotlinx.coroutines.delay

// -----------------------------------------------------------------------------
// MODELO DE DATOS
// -----------------------------------------------------------------------------

private data class SenaComunItem(
    val id: Int,
    val etiqueta: String,
    val emoji: String,
    val palabrasClave: List<String> = emptyList(),
)

private val catalogoSenasComunes = listOf(
    SenaComunItem(1, "Hola", "👋", listOf("hola", "saludo", "buenos")),
    SenaComunItem(2, "Gracias", "🙏", listOf("gracias", "agradezco")),
    SenaComunItem(3, "Ayuda", "🆘", listOf("ayuda", "socorro", "sos")),
    SenaComunItem(4, "Soy Sordo(a)", "👂", listOf("sordo", "sorda", "audición")),
    SenaComunItem(5, "Agua", "💧", listOf("agua", "sed")),
    SenaComunItem(6, "Comida", "🍽️", listOf("comida", "comer", "hambre")),
    SenaComunItem(7, "Baño", "🚻", listOf("baño", "servicio", "wc")),
    SenaComunItem(8, "Sí", "👍", listOf("sí", "ok", "vale")),
    SenaComunItem(9, "No", "👎", listOf("no", "negativo")),
    SenaComunItem(10, "Por favor", "🤲", listOf("por favor", "favor")),
)

private fun motionDesdeCatalogoId(id: Int): AvatarMotion = when (id) {
    1 -> AvatarMotion.SALUDO
    2 -> AvatarMotion.AGRADECIMIENTO
    3 -> AvatarMotion.AYUDA
    4 -> AvatarMotion.SORDO
    5 -> AvatarMotion.AGUA
    6 -> AvatarMotion.COMIDA
    7 -> AvatarMotion.BANO
    8 -> AvatarMotion.SI_GESTO
    9 -> AvatarMotion.NO_GESTO
    10 -> AvatarMotion.POR_FAVOR
    else -> AvatarMotion.DESCONOCIDO
}

private fun motionParaPalabraSuelta(palabraNormalizada: String): AvatarMotion {
    val p = palabraNormalizada.lowercase()
    catalogoSenasComunes.forEach { item ->
        if (item.etiqueta.lowercase() == p) return motionDesdeCatalogoId(item.id)
        if (item.palabrasClave.any { it.equals(p, ignoreCase = true) }) {
            return motionDesdeCatalogoId(item.id)
        }
    }
    return AvatarMotion.DESCONOCIDO
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

// -----------------------------------------------------------------------------
// PANTALLA PRINCIPAL
// -----------------------------------------------------------------------------

/**
 * Pestaña **Texto a Señas**: sección **Palabras** (rejilla filtrada) y **Oraciones** (texto libre + traducir).
 */
@Composable
fun TextoAsenasScreen(
    consultaBuscador: String,
    onMostrarBuscadorCabecera: (Boolean) -> Unit = {},
) {
    var pestaña by rememberSaveable { mutableIntStateOf(0) }
    SideEffect { onMostrarBuscadorCabecera(pestaña == 0) }

    var itemPalabraSeleccionada by remember { mutableStateOf<SenaComunItem?>(null) }
    /** Reinicia el motor 3D al elegir otra tarjeta (aunque el gesto lógico sea similar). */
    var revisionAvatarPalabras by remember { mutableIntStateOf(0) }

    var textoOracion by rememberSaveable { mutableStateOf("") }
    var tituloAvatarOracion by remember { mutableStateOf("Oración a señas") }
    var subtituloAvatarOracion by remember { mutableStateOf<String?>("Escribe texto y pulsa Traducir.") }
    var motionOracion by remember { mutableStateOf(AvatarMotion.NEUTRAL) }
    var jobSecuencia by remember { mutableIntStateOf(0) }
    var secuenciaOracion by remember { mutableStateOf<List<Pair<String, AvatarMotion>>>(emptyList()) }
    var revisionAvatarOracion by remember { mutableIntStateOf(0) }

    LaunchedEffect(jobSecuencia) {
        if (secuenciaOracion.isEmpty()) return@LaunchedEffect
        for ((palabra, motion) in secuenciaOracion) {
            revisionAvatarOracion++
            tituloAvatarOracion = palabra
            motionOracion = motion
            subtituloAvatarOracion = if (motion == AvatarMotion.DESCONOCIDO) {
                "Sin equivalencia en el diccionario demo; gesto neutro."
            } else {
                "Entrada del diccionario demo (referencia LSC-CO orientativa)."
            }
            delay(1350)
        }
        revisionAvatarOracion++
        tituloAvatarOracion = "Secuencia terminada"
        motionOracion = AvatarMotion.NEUTRAL
        subtituloAvatarOracion = "Puedes editar el texto y pulsar Traducir de nuevo."
    }

    val senasFiltradas = remember(consultaBuscador) {
        val q = consultaBuscador.trim()
        if (q.isEmpty()) catalogoSenasComunes
        else catalogoSenasComunes.filter { item ->
            item.etiqueta.contains(q, ignoreCase = true) ||
                item.palabrasClave.any { it.contains(q, ignoreCase = true) }
        }
    }

    val (tituloAvatarPalabras, motionPalabras, subPalabras) = when (val sel = itemPalabraSeleccionada) {
        null -> Triple(
            "Palabra",
            AvatarMotion.NEUTRAL,
            "Elige una palabra de la lista para ver la seña ilustrada.",
        )
        else -> Triple(
            sel.etiqueta,
            motionDesdeCatalogoId(sel.id),
            "Entrada del diccionario demo (referencia LSC-CO orientativa).",
        )
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
                            tituloSeña = tituloAvatarPalabras,
                            subtitulo = subPalabras,
                            motion = motionPalabras,
                            sceneRevision = revisionAvatarPalabras,
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
                            items(senasFiltradas, key = { it.id }) { item ->
                                TarjetaSenaComun(
                                    item = item,
                                    seleccionada = itemPalabraSeleccionada?.id == item.id,
                                    onClick = {
                                        itemPalabraSeleccionada = item
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
                                val tokens = tokenizarOracion(textoOracion)
                                if (tokens.isEmpty()) {
                                    revisionAvatarOracion++
                                    tituloAvatarOracion = "Sin texto"
                                    subtituloAvatarOracion = "Escribe al menos una palabra."
                                    motionOracion = AvatarMotion.NEUTRAL
                                    secuenciaOracion = emptyList()
                                } else {
                                    secuenciaOracion = tokens.map { t ->
                                        t to motionParaPalabraSuelta(t)
                                    }
                                    jobSecuencia++
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
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSenaComun(
    item: SenaComunItem,
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
                Text(text = item.emoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.etiqueta,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}
