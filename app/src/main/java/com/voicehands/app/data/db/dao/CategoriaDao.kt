package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicehands.app.data.db.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(categoria: CategoriaEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(categorias: List<CategoriaEntity>): List<Long>

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun observeAll(): Flow<List<CategoriaEntity>>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun count(): Int
}
