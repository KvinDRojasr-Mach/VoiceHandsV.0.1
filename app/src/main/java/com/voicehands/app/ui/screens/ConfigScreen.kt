package com.voicehands.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.voicehands.app.ui.theme.CelestePrimary

// PANTALLA: Configuración
// En esta vista se gestionan las preferencias del usuario y los permisos reales de hardware.
@Composable
fun ConfigScreen() {
    // 1. PREPARACIÓN DE CONTEXTO Y PERMISOS
    // Se obtiene el contexto de la aplicación para poder lanzar ventanas y verificar permisos.
    val context = LocalContext.current

    // Se verifica el estado real del permiso de la cámara al abrir la pantalla.
    val isCameraGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    // Se inicializa el estado del interruptor de la cámara basado en el permiso real.
    var cameraEnabled by remember { mutableStateOf(isCameraGranted) }

    // Se inicializa el estado para el modo oscuro (por defecto apagado).
    var darkThemeEnabled by remember { mutableStateOf(false) }

    // Se configura el lanzador que mostrará el cuadro de diálogo del sistema pidiendo permiso.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Se actualiza el interruptor dependiendo de si el usuario aceptó o rechazó.
            cameraEnabled = isGranted
        }
    )

    // 2. LÓGICA VISUAL DEL MODO OSCURO
    // Se definen los colores dinámicos que cambiarán al tocar el interruptor.
    val backgroundColor = if (darkThemeEnabled) Color(0xFF121212) else MaterialTheme.colorScheme.background
    val textColor = if (darkThemeEnabled) Color.White else MaterialTheme.colorScheme.onBackground
    val cardColor = if (darkThemeEnabled) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val iconColor = if (darkThemeEnabled) Color.LightGray else Color.Gray

    // 3. ESTRUCTURA VISUAL DE LA PANTALLA
    // Se aplica el color de fondo dinámico al contenedor principal.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // TÍTULO DE LA PANTALLA
        Text(
            text = "Configuración",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        // SECCIÓN 1: PERMISOS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Permisos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                // Ítem: Cámara (Funcional)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = null, tint = iconColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Cámara", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                    }
                    Switch(
                        checked = cameraEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                // Si se intenta encender, se lanza la petición del sistema.
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                // Si se intenta apagar, se envía al usuario a los ajustes del teléfono,
                                // ya que Android no permite quitar permisos mediante código.
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                                // Se revierte visualmente porque el permiso no se ha quitado aún.
                                cameraEnabled = true
                            }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = CelestePrimary)
                    )
                }
            }
        }

        // SECCIÓN 2: PREFERENCIAS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Preferencias",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    // Ítem: Idioma (Estático)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Language, contentDescription = null, tint = iconColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Idioma", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Text(text = "Español", fontSize = 14.sp, color = iconColor, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))

                    // Ítem: Tema oscuro (Funcional localmente)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.DarkMode, contentDescription = null, tint = iconColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Modo Oscuro", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Switch(
                            checked = darkThemeEnabled,
                            onCheckedChange = { darkThemeEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = CelestePrimary)
                        )
                    }
                }
            }
        }

        // SECCIÓN 3: ACERCA DE
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Acerca de",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = iconColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Versión", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                    }
                    Text(text = "0.1", fontSize = 14.sp, color = iconColor)
                }
            }
        }
    }
}