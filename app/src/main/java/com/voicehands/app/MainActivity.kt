package com.voicehands.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

private enum class VoiceHandsMode {
    SIGN_TO_TEXT,
    TEXT_TO_SIGN
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun VoiceHandsHome() {
    var currentMode by remember { mutableStateOf<VoiceHandsMode?>(null) }

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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CelestePrimaryLight,
                            Color.White
                        )
                    )
                )
        ) {
            when (currentMode) {
                null -> VoiceHandsLanding(
                    onSelectMode = { currentMode = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
                VoiceHandsMode.SIGN_TO_TEXT -> SignToTextScreen(onBack = { currentMode = null })
                VoiceHandsMode.TEXT_TO_SIGN -> TextToSignScreen(onBack = { currentMode = null })
            }
        }
    }
}

@Composable
private fun VoiceHandsLanding(
    onSelectMode: (VoiceHandsMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.height(80.dp),
                    shape = CircleShape,
                    color = CelestePrimary.copy(alpha = 0.18f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PanToolAlt,
                            contentDescription = null,
                            tint = CelestePrimaryDark
                        )
                        Spacer(modifier = Modifier.height(0.dp))
                        Icon(
                            imageVector = Icons.Outlined.RecordVoiceOver,
                            contentDescription = null,
                            tint = CelestePrimaryDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Traducción accesible en tiempo real",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = CelestePrimaryDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Conecta lenguaje de señas con texto y voz de forma intuitiva.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeCard(
                title = "Señas a texto / voz",
                description = "Apunta la cámara y deja que VoiceHands interprete el lenguaje de señas.",
                icon = Icons.Outlined.PanToolAlt,
                accentColor = CelestePrimary,
                onClick = { onSelectMode(VoiceHandsMode.SIGN_TO_TEXT) }
            )

            ModeCard(
                title = "Texto / voz a señas",
                description = "Escribe o habla y mira la traducción en señas en la pantalla.",
                icon = Icons.Outlined.RecordVoiceOver,
                accentColor = CelestePrimaryDark,
                onClick = { onSelectMode(VoiceHandsMode.TEXT_TO_SIGN) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Versión 0.1 • Vista previa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "Español",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SignToTextScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Señas a texto / voz",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Esta es una vista previa mínima. Aquí iría la vista de cámara que detecta el lenguaje de señas.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Traducción (ejemplo): \"Hola\"",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: iniciar captura de cámara y traducción real */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelestePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Simular captura de señas")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun TextToSignScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Texto / voz a señas",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Aquí se mostrará la representación en lenguaje de señas (por ejemplo, animaciones o imágenes) según el texto o audio detectado.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { /* TODO: abrir cuadro de texto y sintetizar a señas */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelestePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Escribir texto")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: iniciar reconocimiento de voz */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelestePrimaryDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Hablar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Volver")
            }
        }
    }
}

