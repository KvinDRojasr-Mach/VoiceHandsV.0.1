// Build script de nivel de proyecto para VoiceHands

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    // Plugin de Compose Compiler para Kotlin 2.x
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

