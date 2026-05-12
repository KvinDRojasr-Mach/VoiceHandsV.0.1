import java.io.File
import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VoiceHands"
include(":app")

// -----------------------------------------------------------------------------
// SDK de Android: rutas absolutas no son portables entre PCs.
// Si local.properties apunta a una carpeta que no existe (p. ej. tras un pull),
// se reescribe usando ANDROID_SDK_ROOT / ANDROID_HOME o rutas típicas por SO.
// local.properties debe estar en .gitignore y no subirse al repositorio.
// -----------------------------------------------------------------------------
fun resolveValidAndroidSdk(rootDir: File): File? {
    val localFile = rootDir.resolve("local.properties")
    val props = Properties()
    if (localFile.exists()) {
        try {
            localFile.inputStream().use { props.load(it) }
        } catch (_: Exception) {
            // ignorar
        }
    }
    props.getProperty("sdk.dir")?.trim()?.let(::File)?.takeIf { it.isDirectory }?.let { return it }

    sequenceOf(
        System.getenv("ANDROID_SDK_ROOT"),
        System.getenv("ANDROID_HOME"),
    )
        .mapNotNull { path -> path?.trim()?.takeIf(String::isNotEmpty)?.let(::File) }
        .firstOrNull { it.isDirectory }
        ?.let { return it }

    val home = System.getProperty("user.home")
    val candidates = buildList {
        System.getenv("LOCALAPPDATA")?.let { add(File(it, "Android${File.separator}Sdk")) }
        if (home != null) {
            add(File(home, "AppData${File.separator}Local${File.separator}Android${File.separator}Sdk"))
            add(File(home, "Library/Android/sdk"))
            add(File(home, "Android/Sdk"))
        }
    }
    return candidates.firstOrNull { it.isDirectory }
}

val sdkRoot = resolveValidAndroidSdk(rootDir)
val localPropsFile = rootDir.resolve("local.properties")
val propsLoaded = Properties()
if (localPropsFile.exists()) {
    try {
        localPropsFile.inputStream().use { propsLoaded.load(it) }
    } catch (_: Exception) { }
}
val currentSdk = propsLoaded.getProperty("sdk.dir")?.trim()?.let(::File)
val currentOk = currentSdk?.isDirectory == true

if (!currentOk && sdkRoot != null) {
    val pathForGradle = sdkRoot.absoluteFile.canonicalPath.replace("\\", "/")
    localPropsFile.writeText(
        """
        ## Generado automáticamente: no subas este archivo a Git (.gitignore).
        ## Si cambias de PC, Gradle intentará ANDROID_HOME o la ruta típica del SDK.
        sdk.dir=$pathForGradle

        """.trimIndent() + "\n",
    )
} else if (!currentOk && sdkRoot == null) {
    println(
        "[VoiceHands] No se encontró Android SDK. Instala Android Studio " +
            "o define la variable de entorno ANDROID_HOME (o ANDROID_SDK_ROOT) " +
            "apuntando a la carpeta del SDK.",
    )
}
