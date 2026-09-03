package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicehands.app.data.db.entity.DiccionarioSeniaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiccionarioSeniaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(senia: DiccionarioSeniaEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(senias: List<DiccionarioSeniaEntity>): List<Long>

    @Query(
        """
        SELECT * FROM diccionario_senias
        WHERE activa = 1
        ORDER BY orden ASC, palabraFrase ASC
        """,
    )
    fun observeActivas(): Flow<List<DiccionarioSeniaEntity>>

    @Query(
        """
        SELECT * FROM diccionario_senias
        WHERE activa = 1
          AND (
            palabraFrase LIKE '%' || :consulta || '%'
            OR clave LIKE '%' || :consulta || '%'
            OR idSenia IN (
                SELECT idSenia FROM alias_busqueda
                WHERE alias LIKE '%' || :consulta || '%'
            )
          )
        ORDER BY orden ASC, palabraFrase ASC
        """,
    )
    fun observeActivasFiltradas(consulta: String): Flow<List<DiccionarioSeniaEntity>>

    @Query("SELECT * FROM diccionario_senias WHERE clave = :clave LIMIT 1")
    suspend fun getByClave(clave: String): DiccionarioSeniaEntity?

    @Query("SELECT * FROM diccionario_senias WHERE idSenia = :id LIMIT 1")
    suspend fun getById(id: Long): DiccionarioSeniaEntity?

    @Query("SELECT COUNT(*) FROM diccionario_senias")
    suspend fun count(): Int
}
