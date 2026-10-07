package com.voicehands.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voicehands.app.data.preferences.PreferencesManager
import com.voicehands.app.ui.screens.ConfigScreen
import com.voicehands.app.ui.screens.SenasATextoScreen
import com.voicehands.app.ui.screens.TextoAsenasScreen
import com.voicehands.app.ui.theme.TextOnPrimary
import com.voicehands.app.ui.theme.VoiceHandsTheme

/**
 * Actividad Principal de la aplicación VoiceHands.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val prefsManager = remember { PreferencesManager(applicationContext) }

            var modoOscuro by rememberSaveable { mutableStateOf(prefsManager.modoOscuro) }

            VoiceHandsTheme(darkTheme = modoOscuro) {
                var mostrarAppPrincipal by rememberSaveable { mutableStateOf(false) }

                AnimatedContent(
                    targetState = mostrarAppPrincipal,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        val entrando = fadeIn(animationSpec = tween(durationMillis = 380, delayMillis = 40)) +
                            slideInVertically(
                                animationSpec = tween(durationMillis = 380, delayMillis = 40),
                                initialOffsetY = { it / 10 },
                            )
                        val saliendo = fadeOut(animationSpec = tween(260)) +
                            slideOutVertically(
                                animationSpec = tween(260),
                                targetOffsetY = { -it / 14 },
                            )
                        entrando togetherWith saliendo
                    },
                    label = "transicion_app_ini",
                ) { principal ->
                    if (!principal) {
                        AppIniPantalla(onIniciar = {
                            prefsManager.yaIniciado = true
                            mostrarAppPrincipal = true
                        })
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            VoiceHandsHome(
                                temaOscuro = modoOscuro,
                                onToggleTema = {
                                    val nuevoModo = !modoOscuro
                                    modoOscuro = nuevoModo
                                    prefsManager.modoOscuro = nuevoModo
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pantalla de bienvenida minimalista y onboarding hero.
 */
@Composable
private fun AppIniPantalla(onIniciar: () -> Unit) {
    val colorCabecera = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorCabecera)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.voicehands_logo),
                contentDescription = "Logo de VoiceHands",
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(190.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "VoiceHands",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextOnPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Traducción bidireccional en Lengua de Señas Colombiana (LSC)",
            style = MaterialTheme.typography.bodyMedium,
            color = TextOnPrimary.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onIniciar,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        ) {
            Text(
                text = "Iniciar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Contenedor principal con Navegación Adaptativa (NavigationBar en vertical / NavigationRail en horizontal).
 */
@Composable
fun VoiceHandsHome(
    temaOscuro: Boolean = false,
    onToggleTema: () -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val colorBarraMarca = MaterialTheme.colorScheme.primary
    val tinteSobreMarca = TextOnPrimary
    val tinteSobreMarcaSuave = tinteSobreMarca.copy(alpha = 0.65f)

    if (landscape) {
        // Navegación Horizontal Adaptativa (NavigationRail)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            NavigationRail(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                containerColor = colorBarraMarca,
                contentColor = tinteSobreMarca,
                header = {
                    IconButton(
                        onClick = onToggleTema,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(tinteSobreMarca.copy(alpha = 0.18f)),
                    ) {
                        Icon(
                            imageVector = if (temaOscuro) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = if (temaOscuro) "Activar modo claro" else "Activar modo oscuro",
                            tint = tinteSobreMarca,
                        )
                    }
                },
            ) {
                Spacer(modifier = Modifier.weight(1f))

                NavigationRailItem(
                    icon = { Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Texto a Señas") },
                    label = { Text("Texto a Señas", fontSize = 11.sp) },
                    selected = currentRoute == "texto_a_senas",
                    onClick = { navController.navigate("texto_a_senas") },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = tinteSobreMarca,
                        selectedTextColor = tinteSobreMarca,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = "Señas a Texto") },
                    label = { Text("Señas a Texto", fontSize = 11.sp) },
                    selected = currentRoute == "senas_a_texto",
                    onClick = { navController.navigate("senas_a_texto") },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = tinteSobreMarca,
                        selectedTextColor = tinteSobreMarca,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Config") },
                    label = { Text("Config", fontSize = 11.sp) },
                    selected = currentRoute == "config",
                    onClick = { navController.navigate("config") },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = tinteSobreMarca,
                        selectedTextColor = tinteSobreMarca,
                        indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                        unselectedIconColor = tinteSobreMarcaSuave,
                        unselectedTextColor = tinteSobreMarcaSuave,
                    ),
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            NavHost(
                navController = navController,
                startDestination = "texto_a_senas",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                composable("texto_a_senas") { TextoAsenasScreen() }
                composable("senas_a_texto") { SenasATextoScreen() }
                composable("config") {
                    ConfigScreen(
                        temaOscuro = temaOscuro,
                        onTemaOscuroChange = { activo ->
                            if (activo != temaOscuro) onToggleTema()
                        },
                    )
                }
            }
        }
    } else {
        // Navegación Vertical Estándar (NavigationBar)
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorBarraMarca)
                        .statusBarsPadding(),
                ) {
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
                        IconButton(
                            onClick = onToggleTema,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(tinteSobreMarca.copy(alpha = 0.18f)),
                        ) {
                            Icon(
                                imageVector = if (temaOscuro) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                contentDescription = if (temaOscuro) "Activar modo claro" else "Activar modo oscuro",
                                tint = tinteSobreMarca,
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = colorBarraMarca,
                    tonalElevation = 4.dp,
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Texto a Señas") },
                        label = { Text("Texto a Señas") },
                        selected = currentRoute == "texto_a_senas",
                        onClick = { navController.navigate("texto_a_senas") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = tinteSobreMarca,
                            selectedTextColor = tinteSobreMarca,
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
                            selectedIconColor = tinteSobreMarca,
                            selectedTextColor = tinteSobreMarca,
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
                            selectedIconColor = tinteSobreMarca,
                            selectedTextColor = tinteSobreMarca,
                            indicatorColor = tinteSobreMarca.copy(alpha = 0.22f),
                            unselectedIconColor = tinteSobreMarcaSuave,
                            unselectedTextColor = tinteSobreMarcaSuave,
                        ),
                    )
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "texto_a_senas",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                composable("texto_a_senas") { TextoAsenasScreen() }
                composable("senas_a_texto") { SenasATextoScreen() }
                composable("config") {
                    ConfigScreen(
                        temaOscuro = temaOscuro,
                        onTemaOscuroChange = { activo ->
                            if (activo != temaOscuro) onToggleTema()
                        },
                    )
                }
            }
        }
    }
}
