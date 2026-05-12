// Build script de nivel de proyecto para VoiceHands

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    // Compose Compiler (misma versión que Kotlin; requerido p. ej. por SceneView 4.1.x)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

