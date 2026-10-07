package com.voicehands.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                )
                // Usamos un Callback para insertar datos la primera vez que se crea la BD
                .addCallback(DatabaseCallback(context))
                .build().also { instance = it }
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Cuando la base de datos se crea, lanzamos una corrutina para insertar los datos
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getInstance(context)
                    poblarBaseDeDatos(database)
                }
            }

            suspend fun poblarBaseDeDatos(database: VoiceHandsDatabase) {
                // 1. Insertamos la categoría del Abecedario
                val categoriaDao = database.categoriaDao()
                // Usamos idCategoria = 1 para el Abecedario
                categoriaDao.insert(
                    CategoriaEntity(
                        idCategoria = 1,
                        nombre = "Abecedario",
                        descripcion = "Letras del abecedario en lengua de señas"
                    )
                )

                // 2. Insertamos las señas (letras) apuntando a las imágenes en assets
                val diccionarioDao = database.diccionarioSeniaDao()
                
                // Lista de letras de las que tienes imágenes en assets/abecedario/
                val letrasAbecedario = listOf(
                    "a", "b", "c", "d", "e", "f", "i", "k", "l", "m", 
                    "n", "o", "p", "q", "r", "t", "u", "v", "w", "x", "y"
                )

                var ordenActual = 1
                for (letra in letrasAbecedario) {
                    val rutaImagenAsset = "abecedario/${letra}.png"
                    
                    diccionarioDao.insert(
                        DiccionarioSeniaEntity(
                            clave = letra, // La clave única
                            palabraFrase = letra.uppercase(), // "A", "B", "C"...
                            tipoContenido = "letra",
                            idCategoria = 1,
                            orden = ordenActual++,
                            // Podemos guardar la ruta en metadataDocumental en formato JSON
                            metadataDocumental = """{"ruta_imagen": "$rutaImagenAsset"}"""
                        )
                    )
                }
            }
        }
    }
}
