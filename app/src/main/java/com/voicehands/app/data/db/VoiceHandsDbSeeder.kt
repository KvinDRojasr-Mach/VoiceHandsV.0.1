package com.voicehands.app.data.db

import com.voicehands.app.data.db.entity.AliasBusquedaEntity
import com.voicehands.app.data.db.entity.Animacion3dEntity
import com.voicehands.app.data.db.entity.AvatarRigEntity
import com.voicehands.app.data.db.entity.CategoriaEntity
import com.voicehands.app.data.db.entity.DiccionarioSeniaEntity
import com.voicehands.app.lsc.AvatarAssets

/**
 * Carga inicial del diccionario demo + rig quemado.
 * Las filas de [Animacion3dEntity] quedan con [urlGlb] vacío hasta que subas cada GLB.
 */
object VoiceHandsDbSeeder {

    private data class SeedSenia(
        val clave: String,
        val palabra: String,
        val emoji: String,
        val aliases: List<String>,
        val orden: Int,
        val urlGlb: String = "" // <-- NUEVO: Agregamos la ruta aquí (por defecto vacía)
    )

    private val seniasDemo = listOf(
        // Ya teníamos configurado el Hola:
        SeedSenia("hola", "Hola", "👋", listOf("hola", "saludo", "buenos"), 1, "models/hola_default.glb"),

        // AQUI AGREGAMOS LA RUTA PARA GRACIAS:
        SeedSenia("gracias", "Gracias", "🙏", listOf("gracias", "agradezco"), 2, "models/Gracias_default.glb"),

        // Las demás siguen igual (vacías) por ahora:
        SeedSenia("ayuda", "Ayuda", "🆘", listOf("ayuda", "socorro", "sos"), 3),
        SeedSenia("soy_sordo", "Soy Sordo(a)", "👂", listOf("sordo", "sorda", "audición"), 4),
        SeedSenia("agua", "Agua", "💧", listOf("agua", "sed"), 5),
        SeedSenia("comida", "Comida", "🍽️", listOf("comida", "comer", "hambre"), 6),
        SeedSenia("bano", "Baño", "🚻", listOf("baño", "servicio", "wc"), 7),
        SeedSenia("si", "Sí", "👍", listOf("sí", "ok", "vale"), 8),
        SeedSenia("no", "No", "👎", listOf("no", "negativo"), 9),
        SeedSenia("por_favor", "Por favor", "🤲", listOf("por favor", "favor"), 10),
    )

    suspend fun seedIfEmpty(db: VoiceHandsDatabase) {
        // Si la base de datos ya tiene datos, no hace nada (por eso hay que borrarla para ver cambios)
        if (db.diccionarioSeniaDao().count() > 0) return

        val idCategoria = db.categoriaDao().insert(
            CategoriaEntity(
                nombre = "Comunes",
                descripcion = "Señas frecuentes del diccionario demo LSC-CO",
            ),
        )

        val idRig = db.avatarRigDao().insert(
            AvatarRigEntity(
                codigo = AvatarAssets.RIG_CODIGO,
                nombre = "Avatar masculino base",
                archivoAvatarGlb = AvatarAssets.BASE_GLB,
                clipIdle = "idle",
                activo = true,
            ),
        )

        val aliases = mutableListOf<AliasBusquedaEntity>()
        val animaciones = mutableListOf<Animacion3dEntity>()

        seniasDemo.forEach { seed ->
            val idSenia = db.diccionarioSeniaDao().insert(
                DiccionarioSeniaEntity(
                    clave = seed.clave,
                    palabraFrase = seed.palabra,
                    tipoContenido = "palabra",
                    idCategoria = idCategoria,
                    activa = true,
                    orden = seed.orden,
                    emoji = seed.emoji,
                    metadataDocumental = """{"glosa":"${seed.palabra}","fuente":"demo"}""",
                ),
            )
            seed.aliases.forEach { alias ->
                aliases += AliasBusquedaEntity(idSenia = idSenia, alias = alias)
            }

            // <-- AQUI REEMPLAZAMOS EL STRING VACIO POR LA VARIABLE
            animaciones += Animacion3dEntity(
                idSenia = idSenia,
                idRig = idRig,
                clipName = seed.clave,
                urlGlb = seed.urlGlb,
                formato = "glb",
                duracionMs = 1350,
                version = 1,
                esVigente = true,
            )
        }

        db.aliasBusquedaDao().insertAll(aliases)
        db.animacion3dDao().insertAll(animaciones)
    }

    /** Actualiza el path del avatar quemado si la BD ya existía con el GLB anterior. */
    suspend fun ensureAvatarBase(db: VoiceHandsDatabase) {
        seedIfEmpty(db)
        db.avatarRigDao().updateArchivo(
            codigo = AvatarAssets.RIG_CODIGO,
            path = AvatarAssets.BASE_GLB,
            nombre = "Avatar masculino base",
        )
    }
}