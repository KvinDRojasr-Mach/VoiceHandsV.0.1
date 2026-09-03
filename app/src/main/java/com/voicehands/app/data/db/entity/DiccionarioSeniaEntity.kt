package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diccionario_senias",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["idCategoria"],
            childColumns = ["idCategoria"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["clave"], unique = true),
        Index(value = ["idCategoria"]),
    ],
)
data class DiccionarioSeniaEntity(
    @PrimaryKey(autoGenerate = true) val idSenia: Long = 0,
    /** Clave estable que relaciona el botón con la animación (ej. "hola"). */
    val clave: String,
    val palabraFrase: String,
    /** "palabra" | "oracion" */
    val tipoContenido: String = "palabra",
    val idCategoria: Long,
    val activa: Boolean = true,
    val orden: Int = 0,
    val emoji: String = "",
    /** JSON libre (glosa LSC, notas, etc.). */
    val metadataDocumental: String? = null,
)
