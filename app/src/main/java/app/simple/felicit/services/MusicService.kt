package app.simple.felicit.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import app.simple.felicit.medialoader.AudioGet
import app.simple.felicit.medialoader.MediaLoader
import app.simple.felicit.medialoader.mediaHolders.AudioContent
import kotlin.system.measureTimeMillis


class MusicService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val elapsedTime = measureTimeMillis {
            var list = arrayListOf<AudioContent>()
            list = MediaLoader.withAudioContext(baseContext)
                .getAllAudioContent(AudioGet.externalContentUri)

            for (value in list.indices) {
                println(list[value].art_uri)
            }
        }

        println(elapsedTime)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }
}