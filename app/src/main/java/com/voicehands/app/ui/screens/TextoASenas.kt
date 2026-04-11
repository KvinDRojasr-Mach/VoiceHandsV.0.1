package com.voicehands.app.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicehands.app.ui.theme.CelestePrimaryLight

// -----------------------------------------------------------------------------
// MODELO DE DATOS
// -----------------------------------------------------------------------------

/**
 * Representa una seña frecuente mostrada en la rejilla.
 *
 * @param id Clave estable para LazyVerticalGrid (evita bugs al reordenar/filtrar).
 * @param etiqueta Texto visible bajo el emoji.
 * @param emoji Carácter o secuencia Unicode mostrada en el círculo (sustituible por ilustración).
 * @param palabrasClave Términos extra que el buscador de la cabecera puede usar para filtrar.
 */
private data class SenaComunItem(
    val id: Int,
    val etiqueta: String,
    val emoji: String,
    val palabrasClave: List<String> = emptyList(),
)

/** Lista estática de ejemplo; más adelante puede cargarse desde red o base de datos local. */
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

// -----------------------------------------------------------------------------
// PANTALLA PRINCIPAL
// -----------------------------------------------------------------------------

/**
 * Pestaña **Texto a Señas**: muestra la rejilla de señas comunes filtrada por [consultaBuscador].
 *
 * El título "VoiceHands" y el campo de búsqueda están en [com.voicehands.app.MainActivity] (cabecera compartida);
 * aquí solo se consume el texto de búsqueda para filtrar.
 *
 * @param consultaBuscador Texto que escribe el usuario en el OutlinedTextField de la cabecera.
 */
@Composable
fun TextoAsenasScreen(consultaBuscador: String) {
    // remember(consultaBuscador): recalcula la lista filtrada solo cuando cambia la consulta (eficiente).
    val senasFiltradas = remember(consultaBuscador) {
        val q = consultaBuscador.trim()
        if (q.isEmpty()) catalogoSenasComunes
        else catalogoSenasComunes.filter { item ->
            item.etiqueta.contains(q, ignoreCase = true) ||
                item.palabrasClave.any { it.contains(q, ignoreCase = true) }
        }
    }

    // Column: apila el bloque de contenido verticalmente ocupando todo el alto disponible.
    Column(
        modifier = Modifier
            .fillMaxSize()
            // background del tema: encaja con modo claro u oscuro.
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Surface: bloque con color surface, esquinas superiores redondeadas y sombra muy leve (tonalElevation).
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
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp),
            ) {
                Text(
                    text = "Señas Comunes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))
                // LazyVerticalGrid: rejilla con scroll vertical; 2 columnas fijas.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // items: genera un ítem de grid por cada elemento de senasFiltradas; key evita animaciones raras.
                    items(senasFiltradas, key = { it.id }) { item ->
                        TarjetaSenaComun(item = item)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBCOMPONENTE: tarjeta de una seña
// -----------------------------------------------------------------------------

/**
 * Tarjeta individual: emoji en círculo y etiqueta; colores tomados del [MaterialTheme] (claro/oscuro).
 */
@Composable
private fun TarjetaSenaComun(item: SenaComunItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // clickable: preparado para navegar o reproducir seña cuando exista la lógica.
            .clickable(enabled = true) { /* Reservado: acción al tocar la seña */ },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Círculo con color fijo de marca suavizado (el emoji sigue siendo reconocible en ambos temas).
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
