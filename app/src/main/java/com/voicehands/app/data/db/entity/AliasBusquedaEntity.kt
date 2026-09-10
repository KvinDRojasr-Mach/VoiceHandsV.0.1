package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alias_busqueda",
    foreignKeys = [
        ForeignKey(
            entity = DiccionarioSeniaEntity::class,
            parentColumns = ["idSenia"],
            childColumns = ["idSenia"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["idSenia"]),
        Index(value = ["alias"]),
    ],
)
data class AliasBusquedaEntity(
    @PrimaryKey(autoGenerate = true) val idAlias: Long = 0,
    val idSenia: Long,
    val alias: String,
)
