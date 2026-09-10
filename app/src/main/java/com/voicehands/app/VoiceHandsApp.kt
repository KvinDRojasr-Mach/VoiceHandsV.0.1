package com.voicehands.app

import android.app.Application
import com.voicehands.app.data.db.VoiceHandsDatabase
import com.voicehands.app.data.db.VoiceHandsDbSeeder
import com.voicehands.app.data.repository.SenasRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VoiceHandsApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: VoiceHandsDatabase
        private set

    lateinit var senasRepository: SenasRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = VoiceHandsDatabase.getInstance(this)
        senasRepository = SenasRepository(database)
        appScope.launch {
            VoiceHandsDbSeeder.seedIfEmpty(database)
        }
    }
}
