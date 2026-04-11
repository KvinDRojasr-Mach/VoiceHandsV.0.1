package com.voicehands.app.ui.theme

import androidx.compose.ui.graphics.Color

// --- Marca (azul celeste): usados como primary/secondary en Theme.kt ---

/** Color principal de la marca (botones destacados, cabecera, barra inferior). */
val CelestePrimary = Color(0xFF4FC3F7)

/** Variante más oscura del celeste (texto sobre fondos claros, acentos fuertes). */
val CelestePrimaryDark = Color(0xFF0288D1)

/** Variante más clara (contenedores suaves, indicadores). */
val CelestePrimaryLight = Color(0xFFB3E5FC)

// --- Contraste sobre el azul corporativo (cabecera / nav bar) ---

/** Blanco para iconos y títulos sobre [CelestePrimary]. */
val TextOnPrimary = Color(0xFFFFFFFF)

// --- Modo claro: fondos de pantalla y superficies ---

/** Fondo general muy suave azulado (detrás de las superficies blancas). */
val BackgroundLight = Color(0xFFF5FAFF)

/** Superficie principal tipo “hoja” (tarjetas grandes, campos). */
val SurfaceLight = Color(0xFFFFFFFF)

// --- Texto y tarjetas en la rejilla “Señas comunes” (modo claro) ---

/** Fondo de tarjeta de seña en modo claro (azul muy pálido). */
val SenasComunTarjetaFondo = Color(0xFFF0F7FF)

/** Título de sección y textos fuertes sobre fondo claro. */
val TextoTituloSeccionOscuro = Color(0xFF0A3D62)

/** Texto secundario / placeholder con buen contraste sobre blanco o celeste muy claro. */
val TextoSecundarioContraste = Color(0xFF4A5F72)

// --- Modo oscuro: fondos y variantes (referenciados en darkColorScheme de Theme.kt) ---

/** Fondo general profundo (casi negro azulado). */
val BackgroundDark = Color(0xFF0B1520)

/** Superficie elevada (paneles, hojas sobre el fondo). */
val SurfaceDark = Color(0xFF152535)

/** Variante de superficie para tarjetas secundarias en oscuro. */
val SurfaceVariantDark = Color(0xFF1E3347)
