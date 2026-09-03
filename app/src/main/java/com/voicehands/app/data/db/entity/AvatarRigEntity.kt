package com.voicehands.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "avatar_rig",
    indices = [Index(value = ["codigo"], unique = true)],
)
data class AvatarRigEntity(
    @PrimaryKey(autoGenerate = true) val idRig: Long = 0,
    /** Debe coincidir con el esqueleto de los GLB de animación (ej. "avatar_v1"). */
    val codigo: String,
    val nombre: String,
    /** Path en assets o URL futura del avatar quemado. */
    val archivoAvatarGlb: String,
    val clipIdle: String = "idle",
    val activo: Boolean = true,
)
