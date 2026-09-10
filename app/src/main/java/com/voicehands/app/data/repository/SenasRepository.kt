package com.voicehands.app.data.repository

import com.voicehands.app.data.db.VoiceHandsDatabase
import com.voicehands.app.data.db.entity.Animacion3dEntity
import com.voicehands.app.data.db.entity.AvatarRigEntity
import com.voicehands.app.data.db.entity.DiccionarioSeniaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Acceso al diccionario y a las animaciones 3D.
 * Cuando [Animacion3dEntity.urlGlb] tenga path/URL, el visor podrá cargar ese archivo.
 */
class SenasRepository(
    private val db: VoiceHandsDatabase,
) {
    fun observeSenasActivas(): Flow<List<DiccionarioSeniaEntity>> =
        db.diccionarioSeniaDao().observeActivas()

    fun observeSenasFiltradas(consulta: String): Flow<List<DiccionarioSeniaEntity>> {
        val q = consulta.trim()
        return if (q.isEmpty()) {
            db.diccionarioSeniaDao().observeActivas()
        } else {
            db.diccionarioSeniaDao().observeActivasFiltradas(q)
        }
    }

    suspend fun getSeniaByClave(clave: String): DiccionarioSeniaEntity? =
        db.diccionarioSeniaDao().getByClave(clave)

    suspend fun getAliases(idSenia: Long): List<String> =
        db.aliasBusquedaDao().getAliasesForSenia(idSenia)

    suspend fun getRigActivo(): AvatarRigEntity? =
        db.avatarRigDao().getActivo()

    /**
     * Animación vigente para una seña y el rig activo.
     * [Animacion3dEntity.urlGlb] vacío = aún no hay GLB; usar idle del avatar.
     */
    suspend fun getAnimacionParaClave(clave: String): AnimacionPlayback? {
        val senia = db.diccionarioSeniaDao().getByClave(clave) ?: return null
        val rig = db.avatarRigDao().getActivo() ?: return null
        val anim = db.animacion3dDao().getVigente(senia.idSenia, rig.idRig)
        return AnimacionPlayback(
            senia = senia,
            rig = rig,
            animacion = anim,
        )
    }

    /**
     * Registra o actualiza el archivo GLB de una seña (uso futuro al importar animaciones).
     */
    suspend fun setUrlGlbParaClave(clave: String, urlGlb: String, clipName: String? = null): Boolean {
        val senia = db.diccionarioSeniaDao().getByClave(clave) ?: return false
        val rig = db.avatarRigDao().getActivo() ?: return false
        val actual = db.animacion3dDao().getVigente(senia.idSenia, rig.idRig)
        if (actual != null) {
            db.animacion3dDao().update(
                actual.copy(
                    urlGlb = urlGlb,
                    clipName = clipName ?: actual.clipName,
                    esVigente = true,
                ),
            )
        } else {
            db.animacion3dDao().insert(
                Animacion3dEntity(
                    idSenia = senia.idSenia,
                    idRig = rig.idRig,
                    clipName = clipName ?: clave,
                    urlGlb = urlGlb,
                    formato = "glb",
                    esVigente = true,
                ),
            )
        }
        return true
    }
}

data class AnimacionPlayback(
    val senia: DiccionarioSeniaEntity,
    val rig: AvatarRigEntity,
    val animacion: Animacion3dEntity?,
) {
    /** true si hay un GLB listo para SceneView. */
    val tieneGlb: Boolean
        get() = !animacion?.urlGlb.isNullOrBlank()

    val assetPathOrNull: String?
        get() = animacion?.urlGlb?.takeIf { it.isNotBlank() }

    val clipName: String
        get() = animacion?.clipName ?: rig.clipIdle

    val duracionMs: Int
        get() = animacion?.duracionMs ?: 1350
}
