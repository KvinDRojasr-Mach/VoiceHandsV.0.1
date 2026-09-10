// Build script de nivel de proyecto para VoiceHands

plugins {
    id("com.android.application") version "9.2.1" apply false
    id("com.android.library") version "9.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    // Compose Compiler (misma versión que Kotlin; requerido p. ej. por SceneView 4.1.x)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    // KSP 2.3+ ya no usa el formato kotlin-ksp (p. ej. 2.3.20-2.0.4 no existe).
    id("com.google.devtools.ksp") version "2.3.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

