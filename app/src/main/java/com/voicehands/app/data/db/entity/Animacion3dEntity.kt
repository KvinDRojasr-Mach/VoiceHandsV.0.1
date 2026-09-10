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
     * Ruta local, assets (`models/...`) o URL remota del GLB.
     * Vacío = placeholder hasta subir la animación real.
     */
    val urlGlb: String = "",
    val formato: String = "glb",
    val duracionMs: Int = 1350,
    val version: Int = 1,
    val esVigente: Boolean = true,
)
