package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val idCategoria: Long = 0,
    val nombre: String,
    val descripcion: String? = null,
)
