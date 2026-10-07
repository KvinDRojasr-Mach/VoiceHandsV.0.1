package com.voicehands.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.voicehands.app.data.preferences.PreferencesManager
import com.voicehands.app.ui.theme.CelestePrimary

/**
 * Pantalla de Configuración y Preferencias del usuario.
 */
@Composable
fun ConfigScreen(
    temaOscuro: Boolean = false,
    onTemaOscuroChange: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context.applicationContext) }

    val isCameraGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED

    var cameraEnabled by remember { mutableStateOf(isCameraGranted) }
    var velocidadLsc by remember { mutableStateOf(prefsManager.velocidadLsc) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> cameraEnabled = isGranted },
    )

    val textColor = MaterialTheme.colorScheme.onBackground
    val cardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor,
        )

        // Sección Permisos
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Permisos",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                                cameraEnabled = true
                            }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = CelestePrimary),
                    )
                }
            }
        }

        // Sección Preferencias de Lectura y Tema
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {
                    // Tema Oscuro
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.DarkMode, contentDescription = null, tint = iconColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Modo Oscuro", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Switch(
                            checked = temaOscuro,
                            onCheckedChange = onTemaOscuroChange,
                            colors = SwitchDefaults.colors(checkedTrackColor = CelestePrimary),
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor)

                    // Velocidad de Deletreo LSC
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Speed, contentDescription = null, tint = iconColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Velocidad de Deletreo LSC", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            listOf(
                                0.75f to "Lento (0.7x)",
                                1.0f to "Normal (1.0x)",
                                1.4f to "Rápido (1.4x)",
                            ).forEach { (v, label) ->
                                FilterChip(
                                    selected = (velocidadLsc == v),
                                    onClick = {
                                        velocidadLsc = v
                                        prefsManager.velocidadLsc = v
                                    },
                                    label = { Text(text = label, fontSize = 12.sp) },
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor)

                    // Idioma
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Language, contentDescription = null, tint = iconColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Idioma de Señas", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Text(text = "LSC (Español CO)", fontSize = 14.sp, color = iconColor, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Sección Acerca de
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Acerca de",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = iconColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Versión de la App", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                    }
                    Text(text = "1.0", fontSize = 14.sp, color = iconColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
