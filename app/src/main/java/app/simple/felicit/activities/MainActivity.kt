package app.simple.felicit.activities

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.simple.felicit.R
import app.simple.felicit.interfaces.fragments.FragmentNavigator
import app.simple.felicit.interfaces.fragments.SongsFragmentCallbacks
import app.simple.felicit.medialoader.mediamodels.AudioContent
import app.simple.felicit.services.MusicService
import app.simple.felicit.services.actionQuitService
import app.simple.felicit.ui.dialogs.option.SongOptions
import app.simple.felicit.ui.library.AllSongs
import app.simple.felicit.ui.library.Artists
import app.simple.felicit.ui.library.Home
import com.fragula.Navigator

class MainActivity : AppCompatActivity(), SongsFragmentCallbacks, FragmentNavigator {

    private var isServiceBound: Boolean = false
    private var musicService: MusicService? = null
    private lateinit var navigator: Navigator
    private lateinit var serviceConnection: ServiceConnection
    private lateinit var localBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        navigator = findViewById(R.id.fragment_navigator)
        navigator.addFragment(Home())

        initService()

        localBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == actionQuitService) {
                    finishAffinity()
                }
            }
        }
    }

    /**
     * TODO - improve service implementation
     */
    private fun initService() {

        startService(Intent(applicationContext, MusicService::class.java))

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder: MusicService.MusicBinder = service as MusicService.MusicBinder

                musicService = binder.service

                if (musicService != null) {
                    isServiceBound = true
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }

        bind()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver, IntentFilter(actionQuitService))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            applicationContext.unbindService(serviceConnection)
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
    }

    override fun onBackPressed() {
        if (navigator.fragmentCount > 1) {
            navigator.goToPreviousFragmentAndRemoveLast()
        } else {
            super.onBackPressed()
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
            initService()
        }
    }

    override fun onOptionsPressed(song: AudioContent) {
        SongOptions().newInstance(song, SongOptions.Companion.Source.APP.source).show(supportFragmentManager, "song_options")
    }

    override fun navigateTo(fragmentIndexValue: Int, audioContent: ArrayList<AudioContent>) {
        when (fragmentIndexValue) {
            0 -> {
                navigator.addFragment(AllSongs()) {
                    "all_songs" to audioContent
                }
            }
            1 -> {
                navigator.addFragment(Artists()) {
                    "all_artists" to audioContent
                }
            }
        }
    }
}