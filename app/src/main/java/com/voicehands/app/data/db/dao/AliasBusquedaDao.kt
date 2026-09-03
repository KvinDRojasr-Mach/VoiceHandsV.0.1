package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicehands.app.data.db.entity.AliasBusquedaEntity

@Dao
interface AliasBusquedaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(aliases: List<AliasBusquedaEntity>)

    @Query("SELECT alias FROM alias_busqueda WHERE idSenia = :idSenia")
    suspend fun getAliasesForSenia(idSenia: Long): List<String>
}
