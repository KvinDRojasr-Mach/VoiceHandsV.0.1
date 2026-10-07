package com.voicehands.app.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de preferencias locales del usuario utilizando [SharedPreferences].
 * Almacena de forma permanente configuraciones como el Tema Oscuro/Claro, velocidad de deletreo LSC y el estado de bienvenida.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Preferencia de Tema Oscuro (true = Tema Oscuro, false = Tema Claro).
     */
    var modoOscuro: Boolean
        get() = prefs.getBoolean(KEY_MODO_OSCURO, false)
        set(value) = prefs.edit().putBoolean(KEY_MODO_OSCURO, value).apply()

    /**
     * Indica si el usuario ya presionó "Iniciar" en la pantalla de bienvenida.
     */
    var yaIniciado: Boolean
        get() = prefs.getBoolean(KEY_YA_INICIADO, false)
        set(value) = prefs.edit().putBoolean(KEY_YA_INICIADO, value).apply()

    /**
     * Velocidad predeterminada de deletreo en LSC (0.75f = Lento, 1.0f = Normal, 1.4f = Rápido).
     */
    var velocidadLsc: Float
        get() = prefs.getFloat(KEY_VELOCIDAD_LSC, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_VELOCIDAD_LSC, value).apply()

    companion object {
        private const val PREFS_NAME = "voicehands_preferences"
        private const val KEY_MODO_OSCURO = "key_modo_oscuro"
        private const val KEY_YA_INICIADO = "key_ya_iniciado"
        private const val KEY_VELOCIDAD_LSC = "key_velocidad_lsc"
    }
}
