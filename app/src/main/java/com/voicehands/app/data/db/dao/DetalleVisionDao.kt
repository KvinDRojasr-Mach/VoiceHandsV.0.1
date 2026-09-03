package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicehands.app.data.db.entity.DetalleVisionEntity

@Dao
interface DetalleVisionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(detalle: DetalleVisionEntity)

    @Query("SELECT * FROM detalle_vision WHERE idSenia = :idSenia LIMIT 1")
    suspend fun getBySenia(idSenia: Long): DetalleVisionEntity?
}
