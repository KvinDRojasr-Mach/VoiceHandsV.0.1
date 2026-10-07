package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "animacion_3d",
    foreignKeys = [
        ForeignKey(
            entity = DiccionarioSeniaEntity::class,
            parentColumns = ["idSenia"],
            childColumns = ["idSenia"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AvatarRigEntity::class,
            parentColumns = ["idRig"],
            childColumns = ["idRig"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["idSenia"]),
        Index(value = ["idRig"]),
        Index(value = ["idSenia", "idRig", "esVigente"]),
    ],
)
data class Animacion3dEntity(
    @PrimaryKey(autoGenerate = true) val idAnimacion: Long = 0,
    val idSenia: Long,
    val idRig: Long,
    val clipName: String,
    /**
     * Ruta local de assets/res o URL remota del recurso multimedia (video MP4/WebM, GIF o imagen WebP/PNG).
     * Mantiene retrocompatibilidad con la propiedad [urlGlb].
     */
    val urlGlb: String = "",
    val formato: String = "mp4",
    val duracionMs: Int = 1350,
    val version: Int = 1,
    val esVigente: Boolean = true,
) {
    val urlMedia: String
        get() = urlGlb

    val esVideo: Boolean
        get() = formato.lowercase() in listOf("mp4", "webm", "mkv", "avi") ||
            urlGlb.endsWith(".mp4", ignoreCase = true) ||
            urlGlb.endsWith(".webm", ignoreCase = true)

    val esImagenOGif: Boolean
        get() = formato.lowercase() in listOf("gif", "webp", "png", "jpg", "jpeg") ||
            urlGlb.endsWith(".gif", ignoreCase = true) ||
            urlGlb.endsWith(".webp", ignoreCase = true) ||
            urlGlb.endsWith(".png", ignoreCase = true)
}
