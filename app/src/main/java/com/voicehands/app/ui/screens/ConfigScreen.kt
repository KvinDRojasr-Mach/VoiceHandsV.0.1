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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.voicehands.app.ui.theme.CelestePrimary

/**
 * Preferencias y permisos. El interruptor de tema oscuro usa el mismo estado global
 * que el botón de la cabecera ([temaOscuro] / [onTemaOscuroChange]).
 */
@Composable
fun ConfigScreen(
    temaOscuro: Boolean = false,
    onTemaOscuroChange: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current

    val isCameraGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED

    var cameraEnabled by remember { mutableStateOf(isCameraGranted) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> cameraEnabled = isGranted },
    )

    val textColor = MaterialTheme.colorScheme.onBackground
    val cardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Configuración",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Permisos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(16.dp),
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

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Preferencias",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {
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
                            Text(text = "Idioma", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
                        Text(text = "Español", fontSize = 14.sp, color = iconColor, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor)

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
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Acerca de",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestePrimary,
            )

            Card(
                shape = RoundedCornerShape(16.dp),
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
                        Text(text = "Versión", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                    }
                    Text(text = "0.1", fontSize = 14.sp, color = iconColor)
                }
            }
        }
    }
}
