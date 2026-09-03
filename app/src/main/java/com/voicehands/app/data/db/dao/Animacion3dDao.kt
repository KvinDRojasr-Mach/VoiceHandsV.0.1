package com.voicehands.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.voicehands.app.data.db.entity.Animacion3dEntity

@Dao
interface Animacion3dDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(animacion: Animacion3dEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(animaciones: List<Animacion3dEntity>): List<Long>

    @Update
    suspend fun update(animacion: Animacion3dEntity)

    @Query(
        """
        SELECT * FROM animacion_3d
        WHERE idSenia = :idSenia
          AND idRig = :idRig
          AND esVigente = 1
        ORDER BY version DESC
        LIMIT 1
        """,
    )
    suspend fun getVigente(idSenia: Long, idRig: Long): Animacion3dEntity?

    @Query(
        """
        SELECT a.* FROM animacion_3d a
        INNER JOIN diccionario_senias s ON s.idSenia = a.idSenia
        INNER JOIN avatar_rig r ON r.idRig = a.idRig
        WHERE s.clave = :clave
          AND r.activo = 1
          AND a.esVigente = 1
        ORDER BY a.version DESC
        LIMIT 1
        """,
    )
    suspend fun getVigentePorClave(clave: String): Animacion3dEntity?

    @Query("SELECT COUNT(*) FROM animacion_3d")
    suspend fun count(): Int
}
