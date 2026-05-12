package com.voicehands.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voicehands.app.ui.screens.ConfigScreen
import com.voicehands.app.ui.screens.SenasATextoScreen
import com.voicehands.app.ui.screens.TextoAsenasScreen
import com.voicehands.app.ui.theme.CelestePrimaryDark
import com.voicehands.app.ui.theme.CelestePrimaryLight
import com.voicehands.app.ui.theme.TextOnPrimary
import com.voicehands.app.ui.theme.VoiceHandsTheme

/**
 * Actividad principal de la aplicación.
 *
 * Flujo resumido:
 * 1. [onCreate] registra la UI con Compose.
 * 2. Se guarda si el usuario prefiere tema oscuro ([modoOscuro]) y si ya pasó la pantalla de bienvenida.
 * 3. [AnimatedContent] alterna entre la pantalla de inicio [AppIniPantalla] y el resto de la app [VoiceHandsHome].
 */
// =============================================================================
// ACTIVITY: punto de entrada de Android al abrir la app.
// =============================================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent: todo lo que pongamos aquí es interfaz Compose (no layouts XML).
        setContent {
            // rememberSaveable: sobrevive a rotaciones de pantalla; false = tema claro, true = oscuro.
            var modoOscuro by rememberSaveable { mutableStateOf(false) }

            // VoiceHandsTheme envuelve los colores Material 3 (claro u oscuro) a todo lo interno.
            VoiceHandsTheme(darkTheme = modoOscuro) {
                // Indica si el usuario ya pulsó "Iniciar" en la pantalla de bienvenida.
                var mostrarAppPrincipal by rememberSaveable { mutableStateOf(false) }

                // AnimatedContent: cuando cambia mostrarAppPrincipal, anima el cambio entre dos composables hijos.
                AnimatedContent(
                    targetState = mostrarAppPrincipal,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        // Animación de entrada: aparece con fade y un poco de movimiento vertical.
                        val entrando = fadeIn(animationSpec = tween(durationMillis = 420, delayMillis = 60)) +
                            slideInVertically(
                                animationSpec = tween(durationMillis = 420, delayMillis = 60),
                                initialOffsetY = { it / 10 },
                            )
                        // Animación de salida: la pantalla anterior se desvanece y se desplaza hacia arriba.
                        val saliendo = fadeOut(animationSpec = tween(280)) +
                            slideOutVertically(
                                animationSpec = tween(280),
                                targetOffsetY = { -it / 14 },
                            )
                        // togetherWith: ejecuta salida del estado viejo y entrada del nuevo a la vez.
                        entrando togetherWith saliendo
                    },
                    label = "transicion_app_ini",
                ) { principal ->
                    if (!principal) {
                        // Aún no ha iniciado sesión en la app: mostramos solo la bienvenida.
                        AppIniPantalla(onIniciar = { mostrarAppPrincipal = true })
                    } else {
                        // Surface: lienzo con el color de fondo del tema (cambia en claro/oscuro).
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            // Aquí vive la navegación por pestañas, cabecera y barra inferior.
                            VoiceHandsHome(
                                temaOscuro = modoOscuro,
                                onToggleTema = { modoOscuro = !modoOscuro },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pantalla inicial (splash simple): marca corporativa, hueco para logo y botón para continuar.
 *
 * @param onIniciar Se invoca al pulsar "Iniciar"; la Activity debe mostrar entonces [VoiceHandsHome].
 */
// =============================================================================
// AppIni: primera pantalla tras abrir la app (antes del contenido con pestañas).
// =============================================================================
@Composable
private fun AppIniPantalla(onIniciar: () -> Unit) {
    // primary del tema = azul de marca (mismo tono en claro/oscuro para reconocimiento visual).
    val colorCabecera = MaterialTheme.colorScheme.primary

    // Column: apila elementos en vertical; fillMaxSize ocupa toda la pantalla.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorCabecera)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Spacer con weight empuja el contenido central verticalmente (espacio flexible arriba).
        Spacer(modifier = Modifier.weight(1f))

        // Logo principal centrado y sin fondo para un look limpio/minimalista.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.voicehands_logo),
                contentDescription = "Logo de VoiceHands",
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .height(208.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button de Material3: al hacer clic ejecuta onIniciar (el padre cambia mostrarAppPrincipal).
        Button(
            onClick = onIniciar,
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        ) {
            Text(
                text = "Iniciar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        // Espacio flexible abajo para centrar visualmente el bloque logo + botón.
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Contenedor principal con [Scaffold]: cabecera fija, contenido central (NavHost) y barra de navegación inferior.
 *
 * @param temaOscuro Estado actual del tema (para mostrar icono sol o luna en el interruptor).
 * @param onToggleTema Callback que invierte claro/oscuro (lo eleva MainActivity para envolver VoiceHandsTheme).
 */
// =============================================================================
// VoiceHandsHome: cabecera + pestañas + barra inferior (núcleo de la app tras "Iniciar").
// =============================================================================
@Composable
fun VoiceHandsHome(
    temaOscuro: Boolean = false,
    onToggleTema: () -> Unit = {},
) {
    // rememberNavController: objeto que recuerda la pila de pantallas y permite navegar("ruta").
    val navController = rememberNavController()
    // Observamos la entrada actual del back stack para saber qué pestaña está activa.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Texto del buscador de "Texto a Señas" (solo visible en subpestaña Palabras).
    var consultaBuscador by rememberSaveable { mutableStateOf("") }
    var mostrarBuscadorTextoSena by rememberSaveable { mutableStateOf(true) }

    // Colores fijos sobre el azul corporativo: blanco puro y variantes con alpha para contraste WCAG-friendly.
    val colorBarraMarca = MaterialTheme.colorScheme.primary
    val tinteSobreMarca = TextOnPrimary
    val tinteSobreMarcaSuave = tinteSobreMarca.copy(alpha = 0.62f)
    val tinteSobreMarcaSeleccion = tinteSobreMarca

    // Scaffold: plantilla con ranuras topBar, bottomBar y el cuerpo (lambda final con innerPadding).
    Scaffold(
        topBar = {
            // Column en la cabecera: primera fila título + tema; segunda fila opcional = buscador.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorBarraMarca),
            ) {
                // Fila superior: nombre de la app a la izquierda, interruptor de tema a la derecha.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "VoiceHands",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = tinteSobreMarca,
                        modifier = Modifier.weight(1f),
                    )
                    // IconButton: botón solo con icono; clip(CircleShape) lo hace redondo.
                    IconButton(
                        onClick = onToggleTema,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(tinteSobreMarca.copy(alpha = 0.18f)),
                    ) {
                        // Si ya estamos en oscuro, mostramos el sol (acción = volver a claro); si no, la luna.
                        Icon(
                            imageVector = if (temaOscuro) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = if (temaOscuro) "Activar modo claro" else "Activar modo oscuro",
                            tint = tinteSobreMarca,
                        )
                    }
                }
                // El buscador solo tiene sentido en la pestaña de señas comunes; en las otras no se muestra.
                if (currentRoute == "texto_a_senas" && mostrarBuscadorTextoSena) {
                    // Colores del interior del campo: van sobre fondo "surface" (blanco en claro), no sobre el azul.
                    // Por eso NO usamos tinteSobreMarcaSuave (blanco semitransparente): se perdería sobre blanco.
                    val colorTextoCampo = MaterialTheme.colorScheme.onSurface
                    val colorSecundarioCampo = MaterialTheme.colorScheme.onSurfaceVariant

                    OutlinedTextField(
                        value = consultaBuscador,
                        onValueChange = { consultaBuscador = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
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
                        // OutlinedTextFieldDefaults.colors: fondo surface + texto oscuro legible en modo claro.
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
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
            }
        },
        bottomBar = {
            // NavigationBar: barra inferior de Material3 con tres destinos (pestañas).
            NavigationBar(
                containerColor = colorBarraMarca,
                tonalElevation = 6.dp,
            ) {
                // Cada NavigationBarItem: un icono + etiqueta; selected pinta el estado activo.
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Texto a Señas") },
                    label = { Text("Texto a Señas") },
                    selected = currentRoute == "texto_a_senas",
                    onClick = { navController.navigate("texto_a_senas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = tinteSobreMarcaSeleccion,
                        selectedTextColor = tinteSobreMarcaSeleccion,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = "Señas a Texto") },
                    label = { Text("Señas a Texto") },
                    selected = currentRoute == "senas_a_texto",
                    onClick = { navController.navigate("senas_a_texto") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = tinteSobreMarcaSeleccion,
                        selectedTextColor = tinteSobreMarcaSeleccion,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Config") },
                    label = { Text("Config") },
                    selected = currentRoute == "config",
                    onClick = { navController.navigate("config") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = tinteSobreMarcaSeleccion,
                        selectedTextColor = tinteSobreMarcaSeleccion,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )
            }
        },
    ) { innerPadding ->
        // innerPadding: evita que el contenido quede debajo de la topBar o bottomBar del Scaffold.
        // NavHost: asocia rutas string a composables; solo uno visible a la vez.
        NavHost(
            navController = navController,
            startDestination = "texto_a_senas",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // composable("ruta") { ... }: define una pantalla en el grafo de navegación.
            composable("texto_a_senas") {
                TextoAsenasScreen(
                    consultaBuscador = consultaBuscador,
                    onMostrarBuscadorCabecera = { mostrarBuscadorTextoSena = it },
                )
            }
            composable("senas_a_texto") { SenasATextoScreen() }
            composable("config") { ConfigScreen() }
        }
    }
}
