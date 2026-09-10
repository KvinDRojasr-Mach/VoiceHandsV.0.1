package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicehands.app.data.db.entity.AvatarRigEntity

@Dao
interface AvatarRigDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rig: AvatarRigEntity): Long

    @Query("SELECT * FROM avatar_rig WHERE activo = 1 LIMIT 1")
    suspend fun getActivo(): AvatarRigEntity?

    @Query("SELECT * FROM avatar_rig WHERE codigo = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): AvatarRigEntity?
}
