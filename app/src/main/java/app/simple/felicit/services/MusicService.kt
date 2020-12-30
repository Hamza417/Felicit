package app.simple.felicit.services

import android.app.*
import android.content.*
import android.media.*
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import app.simple.felicit.R
import app.simple.felicit.activities.MainActivity
import app.simple.felicit.loader.scanSongList
import app.simple.felicit.medialoader.mediamodels.AudioContent
import app.simple.felicit.util.ImageHelper.getBitmapFromUriForNotifications
import app.simple.felicit.util.IntentHelper.sendLocalBroadcastIntent
import java.io.IOException
import java.util.*
import kotlin.math.ln

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener {

    private val volumeFadeDuration: Int = 250
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    private var notificationChannel: NotificationChannel? = null
    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null

    private lateinit var songs: MutableList<AudioContent>

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    // Variables
    var songPosition = 0
    private var iVolume = 0
    private val intVolumeMax = 100
    private val intVolumeMin = 0
    private val floatVolumeMax = 1f
    private val floatVolumeMin = 0f
    private val notificationId = 2548
    private var wasPlaying = true

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        registerBecomingNoisyReceiver()
        setupMediaSession()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MediaButtonIntentReceiver.handleIntent(baseContext, intent)

        when (intent.action) {
            actionPlay -> play()
            actionPause -> pause()
            actionNext -> playNextSong()
            actionPrevious -> playPreviousSong()
            actionQuitService -> onDestroy()
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        @Suppress("Deprecation")
        audioManager.abandonAudioFocus(this)
        mediaPlayer?.release()
        notificationManager?.cancel(notificationId)
        unregisterReceiver(becomingNoisyReceiver)
        sendLocalBroadcastIntent(actionQuitService, this)
        stopSelf()
    }

    private fun setupMediaSession() {
        val mediaButtonReceiverComponentName = ComponentName(applicationContext, MediaButtonIntentReceiver::class.java)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.component = mediaButtonReceiverComponentName
        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)
        mediaSessionCompat = MediaSessionCompat(this, "Felicit", mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent)
        mediaSessionCompat.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                play()
            }

            override fun onPause() {
                pause()
            }

            override fun onSkipToNext() {
                playNextSong()
            }

            override fun onSkipToPrevious() {
                playPreviousSong()
            }

            override fun onStop() {
                quit()
            }

            override fun onSeekTo(pos: Long) {
                seek(pos.toInt())
            }

            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                return MediaButtonIntentReceiver.handleIntent(this@MusicService, mediaButtonEvent)
            }
        })

        @Suppress("deprecation")
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        mediaSessionCompat.setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
        mediaSessionCompat.isActive = true
        //mediaControllerCompat = mediaSessionCompat.controller
        //mediaMetadataCompat = mediaControllerCompat.metadata
    }

    fun initMediaPlayer() {
        try {
            if (songs.size != 0 && mediaPlayer != null) {
                mediaPlayer!!.reset()
                mediaPlayer!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                mediaPlayer!!.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                mediaPlayer!!.setOnPreparedListener(this)
                mediaPlayer!!.setOnCompletionListener(this)
                mediaPlayer!!.setOnErrorListener(this)
                mediaPlayer!!.setDataSource(baseContext, Uri.parse(songs[songPosition].fileStringUri))
                mediaPlayer!!.prepareAsync()
            }
        } catch (ignored: IOException) {
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: IllegalStateException) {
        } catch (ignored: SecurityException) {
        }
    }

    fun pause() {
        if (timerTask != null && timer != null) {
            timer!!.cancel()
            timerTask!!.cancel()
        }

        // Set current volume, depending on fade or not
        iVolume = if (volumeFadeDuration > 0) {
            intVolumeMax
        } else {
            intVolumeMin
        }
        updateVolume(0)

        // Start increasing volume in increments
        if (volumeFadeDuration > 0) {
            timer = Timer(true)
            timerTask = object : TimerTask() {
                override fun run() {
                    updateVolume(-1)
                    if (iVolume == intVolumeMin) {
                        // Pause music
                        if (mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.pause()
                            showNotification(generateAction(R.drawable.ic_play, "Play", actionPlay))
                            setPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                            stopForeground(false)
                        }
                        timer!!.cancel()
                        timer!!.purge()
                    }
                }
            }

            // calculate delay, cannot be zero, set to 1 if zero
            var delay: Int = volumeFadeDuration / intVolumeMax
            if (delay == 0) {
                delay = 1
            }
            timer!!.schedule(timerTask, delay.toLong(), delay.toLong())
        }
    }

    private fun setPlaybackState(playbackState: Int) {
        mediaSessionCompat.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(playbackState, mediaPlayer!!.currentPosition.toLong(), 1f)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
        )
    }

    fun play() {
        if (timerTask != null && timer != null) {
            timer!!.cancel()
            timerTask!!.cancel()
        }

        // Set current volume, depending on fade or not
        iVolume = if (volumeFadeDuration > 0) {
            intVolumeMin
        } else {
            intVolumeMax
        }
        updateVolume(0)

        // Play music
        if (!mediaPlayer!!.isPlaying) {
            if (requestAudioFocus()) {
                mediaPlayer!!.start()
                showNotification(generateAction(R.drawable.ic_pause, "Pause", actionPause))
                setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
        }

        // Start increasing volume in increments
        if (volumeFadeDuration > 0) {
            timer = Timer(true)
            timerTask = object : TimerTask() {
                override fun run() {
                    updateVolume(1)
                    if (iVolume == intVolumeMax) {
                        timer!!.cancel()
                        timer!!.purge()
                    }
                }
            }

            // calculate delay, cannot be zero, set to 1 if zero
            var delay: Int = volumeFadeDuration / intVolumeMax
            if (delay == 0) {
                delay = 1
            }
            timer!!.schedule(timerTask, delay.toLong(), delay.toLong())
        }
    }

    fun playNextSong() {
        if (songPosition < songs.size - 1) {
            songPosition++
        } else {
            songPosition = 0
        }

        initMediaPlayer()
    }

    fun playPreviousSong() {
        if (songPosition >= 1) {
            songPosition--
        } else {
            songPosition = songs.size - 1
        }

        initMediaPlayer()
    }

    fun seek(position: Int) {
        mediaPlayer!!.seekTo(position)
        setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
    }

    fun quit() {
        this.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        play()

        val mediaMetadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songs[songPosition].title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songs[songPosition].artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songs[songPosition].album)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songs[songPosition].artUri)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mp!!.duration.toLong())
            .build()

        mediaSessionCompat.setMetadata(mediaMetadata)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK,
            MediaPlayer.MEDIA_ERROR_SERVER_DIED,
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> playNextSong()
        }
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playNextSong()
    }

    private fun updateVolume(change: Int) {
        // increment or decrement depending on type of fade
        iVolume += change

        // ensure iVolume within boundaries
        if (iVolume < intVolumeMin) {
            iVolume = intVolumeMin
        } else if (iVolume > intVolumeMax) {
            iVolume = intVolumeMax
        }

        // convert to float value
        var fVolume = 1 - ln((intVolumeMax - iVolume).toDouble()).toFloat() / ln(intVolumeMax.toDouble()).toFloat()

        // ensure fVolume within boundaries
        if (fVolume < floatVolumeMin) {
            fVolume = floatVolumeMin
        } else if (fVolume > floatVolumeMax) {
            fVolume = floatVolumeMax
        }
        mediaPlayer!!.setVolume(fVolume, fVolume)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                wasPlaying = mediaPlayer!!.isPlaying
                pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (wasPlaying) {
                    play()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer!!.setVolume(0.2f, 0.2f)
        }
    }

    private fun requestAudioFocus(): Boolean {
        val value: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            value = audioManager.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                                       .setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                                                       .setAcceptsDelayedFocusGain(true)
                                                       .setWillPauseWhenDucked(true)
                                                       .setOnAudioFocusChangeListener(this)
                                                       .build())
        } else {
            @Suppress("deprecation") // This one's deprecated, other one won't work
            value = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, 0)
        }

        return value == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun setSongs(songs: MutableList<AudioContent>) {
        this.songs = songs
        scanSongList(songs, applicationContext)
    }

    private fun showNotification(action: NotificationCompat.Action) {
        val channelId = "007"
        var importance = 0
        val name: CharSequence = "Felicit Music Player" // The user-visible name of the channel.
        notificationManager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_LOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, name, importance)
            notificationManager!!.createNotificationChannel(notificationChannel!!)
        }

        val intentAction = Intent(this, MainActivity::class.java)
        intentAction.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        intentAction.putExtra(actionOpen, "Open")

        val buttonClick = PendingIntent.getActivity(this, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_icon_notifications)
            .setLargeIcon(getBitmapFromUriForNotifications(baseContext, songs[songPosition].artUri))
            .addAction(generateAction(R.drawable.ic_prev, "Previous", actionPrevious))
            .addAction(action) /* Play Pause Action */
            .addAction(generateAction(R.drawable.ic_next, "Next", actionNext))
            .addAction(generateAction(R.drawable.ic_cross, "Close", actionQuitService))
            .setContentTitle(songs[songPosition].title)
            .setContentText(songs[songPosition].artist)
            .setSubText(songs[songPosition].album)
            .setContentIntent(buttonClick)
            .setShowWhen(false)
            .setColorized(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.sessionToken))
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notification: Notification = builder!!.build()
        notificationManager!!.notify(notificationId, notification)
        startForeground(notificationId, notification)
    }

    private fun generateAction(icon: Int, title: String, intentAction: String): NotificationCompat.Action {
        val intent = Intent(this, MusicService::class.java)
        intent.action = intentAction
        val pendingIntent = PendingIntent.getService(this, 1, intent, 0)
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    fun isPlaying(): Boolean {
        if (mediaPlayer == null) return false
        return mediaPlayer!!.isPlaying
    }

    fun getCurrentSong(): AudioContent {
        return songs[songPosition]
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        this.registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(mediaPlayer!!.isPlaying) {
                pause()
            }
        }
    }
}