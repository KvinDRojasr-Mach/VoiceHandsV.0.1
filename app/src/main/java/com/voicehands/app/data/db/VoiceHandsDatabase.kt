package com.voicehands.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.voicehands.app.data.db.dao.AliasBusquedaDao
import com.voicehands.app.data.db.dao.Animacion3dDao
import com.voicehands.app.data.db.dao.AvatarRigDao
import com.voicehands.app.data.db.dao.CategoriaDao
import com.voicehands.app.data.db.dao.DetalleVisionDao
import com.voicehands.app.data.db.dao.DiccionarioSeniaDao
import com.voicehands.app.data.db.entity.AliasBusquedaEntity
import com.voicehands.app.data.db.entity.Animacion3dEntity
import com.voicehands.app.data.db.entity.AvatarRigEntity
import com.voicehands.app.data.db.entity.CategoriaEntity
import com.voicehands.app.data.db.entity.DetalleVisionEntity
import com.voicehands.app.data.db.entity.DiccionarioSeniaEntity

@Database(
    entities = [
        CategoriaEntity::class,
        DiccionarioSeniaEntity::class,
        AliasBusquedaEntity::class,
        AvatarRigEntity::class,
        Animacion3dEntity::class,
        DetalleVisionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class VoiceHandsDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun diccionarioSeniaDao(): DiccionarioSeniaDao
    abstract fun aliasBusquedaDao(): AliasBusquedaDao
    abstract fun avatarRigDao(): AvatarRigDao
    abstract fun animacion3dDao(): Animacion3dDao
    abstract fun detalleVisionDao(): DetalleVisionDao

    companion object {
        const val NAME = "voicehands.db"

        @Volatile
        private var instance: VoiceHandsDatabase? = null

        fun getInstance(context: Context): VoiceHandsDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    VoiceHandsDatabase::class.java,
                    NAME,
                ).build().also { instance = it }
            }
        }
    }
}
