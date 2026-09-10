package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Datos para Señas a texto / IA. No alimenta el visor 3D.
 */
@Entity(
    tableName = "detalle_vision",
    foreignKeys = [
        ForeignKey(
            entity = DiccionarioSeniaEntity::class,
            parentColumns = ["idSenia"],
            childColumns = ["idSenia"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class DetalleVisionEntity(
    @PrimaryKey val idSenia: Long,
    val puntosReferenciaMano: String? = null,
    val versionModeloIa: String? = null,
)
