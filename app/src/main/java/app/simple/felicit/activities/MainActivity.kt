package app.simple.felicit.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import app.simple.felicit.R
import app.simple.felicit.interfaces.fragments.SongsFragmentCallbacks
import app.simple.felicit.medialoader.mediaHolders.AudioContent
import app.simple.felicit.services.MusicService
import app.simple.felicit.ui.library.AllSongs
import app.simple.felicit.ui.library.Home

class MainActivity : AppCompatActivity(), SongsFragmentCallbacks {

    private var isServiceBound: Boolean = false
    private var musicService: MusicService? = null
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val home = supportFragmentManager.findFragmentByTag("home")

        if (home == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_navigator, Home().newInstance(), "home")
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_navigator, home, "home")
                .commit()
        }
    }

    /**
     * TODO - improve service implementation
     */
    private fun initService(songs: MutableList<AudioContent>, position: Int, id: Int?) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, MusicService::class.java))
        } else {
            startService(Intent(applicationContext, MusicService::class.java))
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder: MusicService.MusicBinder = service as MusicService.MusicBinder

                musicService = binder.service

                if (musicService != null) {
                    isServiceBound = true
                    onSongClicked(songs, position, id)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }

        bind()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            applicationContext.unbindService(serviceConnection)
        }
    }

    private fun bind() {
        applicationContext.bindService(Intent(applicationContext, MusicService::class.java), serviceConnection, Context.BIND_ABOVE_CLIENT)
    }

    override fun onSongClicked(songs: MutableList<AudioContent>, position: Int, id: Int?) {
        if (isServiceBound) {
            musicService?.setSongs(songs)
            musicService?.songPosition = position
            musicService?.initMediaPlayer()
        } else {
            initService(songs, position, id)
        }
    }
}