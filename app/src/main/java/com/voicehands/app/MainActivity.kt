package com.voicehands.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

//  importar los 3 archivos nuevos que se crearon en las nuevas carpetas
import com.voicehands.app.ui.screens.ConfigScreen
import com.voicehands.app.ui.screens.TextoAVozScreen
import com.voicehands.app.ui.screens.TraducirScreen

import com.voicehands.app.ui.theme.CelestePrimary
import com.voicehands.app.ui.theme.CelestePrimaryDark
import com.voicehands.app.ui.theme.CelestePrimaryLight
import com.voicehands.app.ui.theme.VoiceHandsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceHandsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VoiceHandsHome()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VoiceHandsHome() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "VoiceHands",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CelestePrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Texto a Voz") },
                    label = { Text("Texto a Voz") },
                    selected = currentRoute == "texto_voz",
                    onClick = { navController.navigate("texto_voz") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CelestePrimaryDark,
                        selectedTextColor = CelestePrimaryDark,
                        indicatorColor = CelestePrimaryLight.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = "Traducir") },
                    label = { Text("Traducir") },
                    selected = currentRoute == "traducir",
                    onClick = { navController.navigate("traducir") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CelestePrimaryDark,
                        selectedTextColor = CelestePrimaryDark,
                        indicatorColor = CelestePrimaryLight.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Config") },
                    label = { Text("Config") },
                    selected = currentRoute == "config",
                    onClick = { navController.navigate("config") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CelestePrimaryDark,
                        selectedTextColor = CelestePrimaryDark,
                        indicatorColor = CelestePrimaryLight.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { innerPadding ->
        // El NavHost ahora llama a las funciones que viven en los otros archivos
        NavHost(
            navController = navController,
            startDestination = "traducir",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("texto_voz") { TextoAVozScreen() }
            composable("traducir") { TraducirScreen() }
            composable("config") { ConfigScreen() }
        }
    }
}