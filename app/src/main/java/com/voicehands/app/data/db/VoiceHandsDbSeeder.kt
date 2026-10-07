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
        val urlMedia: String = "",
        val formato: String = "mp4",
    )

    private val seniasDemo = listOf(
        // Catálogo de Letras del Abecedario LSC en estricto orden alfabético
        SeedSenia("a", "A", "🅰️", listOf("a", "letra a"), 1, "abecedario/a.png", "png"),
        SeedSenia("b", "B", "🅱️", listOf("b", "letra b"), 2, "abecedario/b.png", "png"),
        SeedSenia("c", "C", "🔤", listOf("c", "letra c"), 3, "abecedario/c.png", "png"),
        SeedSenia("d", "D", "🔤", listOf("d", "letra d"), 4, "abecedario/d.png", "png"),
        SeedSenia("e", "E", "🔤", listOf("e", "letra e"), 5, "abecedario/e.png", "png"),
        SeedSenia("f", "F", "🔤", listOf("f", "letra f"), 6, "abecedario/f.png", "png"),
        SeedSenia("i", "I", "🔤", listOf("i", "letra i"), 7, "abecedario/i.png", "png"),
        SeedSenia("k", "K", "🔤", listOf("k", "letra k"), 8, "abecedario/k.png", "png"),
        SeedSenia("l", "L", "🔤", listOf("l", "letra l"), 9, "abecedario/l.png", "png"),
        SeedSenia("m", "M", "🔤", listOf("m", "letra m"), 10, "abecedario/m.png", "png"),
        SeedSenia("n", "N", "🔤", listOf("n", "letra n"), 11, "abecedario/n.png", "png"),
        SeedSenia("o", "O", "🌰", listOf("o", "letra o"), 12, "abecedario/o.png", "png"),
        SeedSenia("p", "P", "🅿️", listOf("p", "letra p"), 13, "abecedario/p.png", "png"),
        SeedSenia("q", "Q", "🔤", listOf("q", "letra q"), 14, "abecedario/q.png", "png"),
        SeedSenia("r", "R", "🔤", listOf("r", "letra r"), 15, "abecedario/r.png", "png"),
        SeedSenia("t", "T", "🔤", listOf("t", "letra t"), 16, "abecedario/t.png", "png"),
        SeedSenia("u", "U", "🔤", listOf("u", "letra u"), 17, "abecedario/u.png", "png"),
        SeedSenia("v", "V", "✌️", listOf("v", "letra v"), 18, "abecedario/v.png", "png"),
        SeedSenia("w", "W", "🔤", listOf("w", "letra w"), 19, "abecedario/w.png", "png"),
        SeedSenia("x", "X", "🔤", listOf("x", "letra x"), 20, "abecedario/x.png", "png"),
        SeedSenia("y", "Y", "🔤", listOf("y", "letra y"), 21, "abecedario/y.png", "png"),

        // Señas completas adicionales
        SeedSenia("hola", "Hola", "👋", listOf("hola", "saludo", "buenos"), 101, "videos/hola_default.mp4", "mp4"),
        SeedSenia("gracias", "Gracias", "🙏", listOf("gracias", "agradezco"), 102, "videos/Gracias_default.mp4", "mp4"),
        SeedSenia("por_favor", "Por favor", "🤲", listOf("por favor", "favor"), 103),
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
                archivoAvatarGlb = AvatarAssets.BASE_IMAGE,
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
                urlGlb = seed.urlMedia,
                formato = seed.formato,
                duracionMs = 1350,
                version = 1,
                esVigente = true,
            )
        }

        db.aliasBusquedaDao().insertAll(aliases)
        db.animacion3dDao().insertAll(animaciones)
    }

    /** Actualiza el path del avatar quemado y sincroniza letras del abecedario. */
    suspend fun ensureAvatarBase(db: VoiceHandsDatabase) {
        seedIfEmpty(db)
        db.avatarRigDao().updateArchivo(
            codigo = AvatarAssets.RIG_CODIGO,
            path = AvatarAssets.BASE_IMAGE,
            nombre = "Avatar masculino base",
        )

        // Sincronizar las letras del abecedario si la BD ya existía previamente
        val rig = db.avatarRigDao().getActivo() ?: return
        val idCat = 1L
        seniasDemo.filter { it.urlMedia.startsWith("abecedario/") }.forEach { seed ->
            val senia = db.diccionarioSeniaDao().getByClave(seed.clave)
            if (senia == null) {
                val idSenia = db.diccionarioSeniaDao().insert(
                    DiccionarioSeniaEntity(
                        clave = seed.clave,
                        palabraFrase = seed.palabra,
                        tipoContenido = "letra",
                        idCategoria = idCat,
                        activa = true,
                        orden = seed.orden,
                        emoji = seed.emoji,
                        metadataDocumental = """{"glosa":"${seed.palabra}","fuente":"demo"}""",
                    )
                )
                db.aliasBusquedaDao().insertAll(listOf(AliasBusquedaEntity(idSenia = idSenia, alias = seed.clave)))
                db.animacion3dDao().insert(
                    Animacion3dEntity(
                        idSenia = idSenia,
                        idRig = rig.idRig,
                        clipName = seed.clave,
                        urlGlb = seed.urlMedia,
                        formato = seed.formato,
                        duracionMs = 950,
                        esVigente = true,
                    )
                )
            } else {
                val anim = db.animacion3dDao().getVigente(senia.idSenia, rig.idRig)
                if (anim != null && anim.urlGlb != seed.urlMedia) {
                    db.animacion3dDao().update(anim.copy(urlGlb = seed.urlMedia, formato = seed.formato))
                } else if (anim == null) {
                    db.animacion3dDao().insert(
                        Animacion3dEntity(
                            idSenia = senia.idSenia,
                            idRig = rig.idRig,
                            clipName = seed.clave,
                            urlGlb = seed.urlMedia,
                            formato = seed.formato,
                            duracionMs = 950,
                            esVigente = true,
                        )
                    )
                }
            }
        }
    }
}