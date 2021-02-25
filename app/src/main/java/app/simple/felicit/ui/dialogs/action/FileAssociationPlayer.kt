package app.simple.felicit.ui.dialogs.action

import android.animation.ObjectAnimator
import android.content.*
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import app.simple.felicit.R
import app.simple.felicit.decoration.views.CustomBottomSheetDialogFragment
import app.simple.felicit.glide.modules.AudioCoverUtil.loadFromFileDescriptor
import app.simple.felicit.helper.AudioHelper.getBitrate
import app.simple.felicit.helper.AudioHelper.getSampling
import app.simple.felicit.helper.MetadataHelper
import app.simple.felicit.helper.NumberHelper.getFormattedTime
import app.simple.felicit.helper.UriHelper.getFileExtension
import app.simple.felicit.preference.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class FileAssociationPlayer : CustomBottomSheetDialogFragment(), OnAudioFocusChangeListener {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var handler: Handler? = null
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null
    private var ongoingCall = false
    private var songUri: Uri? = null
    private var animation: ObjectAnimator? = null

    private lateinit var title: TextView
    private lateinit var artist: TextView
    private lateinit var album: TextView
    private lateinit var info: TextView
    private lateinit var duration: TextView
    private lateinit var currentProgress: TextView
    private lateinit var mimeImageView: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var container: FrameLayout

    private lateinit var playButton: ImageButton
    private lateinit var menuButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        SharedPreferences.init(requireContext())
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return inflater.inflate(R.layout.dialog_mime_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songUri = Uri.parse(arguments?.getString("file_content_uri"))

        mimeImageView = view.findViewById(R.id.album_art_mime)
        title = view.findViewById(R.id.mime_title)
        artist = view.findViewById(R.id.mime_artist)
        album = view.findViewById(R.id.mime_album)
        info = view.findViewById(R.id.mime_file_info)
        container = view.findViewById(R.id.mime_dialog)

        playButton = view.findViewById(R.id.mimi_play_button)
        menuButton = view.findViewById(R.id.mime_menu_button)

        duration = view.findViewById(R.id.current_duration_mime)
        currentProgress = view.findViewById(R.id.current_time_mime)
        seekBar = view.findViewById(R.id.seekbar_mime)
        handler = Handler(Looper.getMainLooper())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(this)
                .build()
        }

        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        registerBecomingNoisyReceiver()
        callStateListener()
        getRawData()
        audioPlayer()

        container.setOnClickListener {
            playPause()
        }

        playButton.setOnClickListener {
            playPause()
        }

        menuButton.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu)
            val popup = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                PopupMenu(wrapper, menuButton, 0, 0, 0)
            } else {
                PopupMenu(requireContext(), menuButton)
            }
            popup.menuInflater.inflate(R.menu.mime_menu, popup.menu)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.gravity = Gravity.END
            }

            //this.view?.animate()?.alpha(0.9F)?.setDuration(1000L)?.setInterpolator(DecelerateInterpolator())?.start()

            popup.setOnMenuItemClickListener { item ->

                true
            }

            popup.setOnDismissListener {
                //this.view?.animate()?.alpha(1F)?.setDuration(1000L)?.setInterpolator(DecelerateInterpolator())?.start()
            }

            popup.show()
        }

        mediaPlayer.setOnPreparedListener {
            if (requestAudioFocus()) {
                mediaPlayer.start()
            }
            handler!!.post(update)
            seekBar.max = mediaPlayer.duration
            duration.text = getFormattedTime(seekBar.max.toLong())
        }

        mediaPlayer.setOnCompletionListener {
            dialog!!.dismiss()
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentProgress.text = getFormattedTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler!!.removeCallbacks(update)
                animation?.cancel()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler!!.post(update)
                mediaPlayer.seekTo(seekBar.progress)
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        try {
            mediaPlayer.reset()
            mediaPlayer.release()
            mimeImageView.clearAnimation()
            removeAudioFocus()
            audioManager = null
            handler!!.removeCallbacks(update)
            requireContext().unregisterReceiver(becomingNoisyReceiver)
            if (childFragmentManager.backStackEntryCount > 1) {
                childFragmentManager.popBackStackImmediate()
            }
            requireActivity().finishAfterTransition()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun audioPlayer() {
        try {
            mediaPlayer.setDataSource(requireContext(), songUri!!)
            mediaPlayer.prepareAsync()
        } catch (e: IllegalStateException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        } catch (e: IOException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }
    }

    private fun playPause() {
        if (!ongoingCall) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.ic_play)
                handler!!.removeCallbacks(update)
                removeAudioFocus()
                mimeImageView.animate()
                    .alpha(0.6f)
                    .setDuration(1500)
                    .start()
            } else if (!mediaPlayer.isPlaying && requestAudioFocus()) {
                handler!!.post(update)
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.ic_pause)
                mimeImageView.animate()
                    .alpha(1f)
                    .setDuration(1500)
                    .start()
            }
        }
    }

    private val update: Runnable = object : Runnable {
        override fun run() {
            setSeekbarProgress(mediaPlayer.currentPosition)
            currentProgress.text = getFormattedTime(seekBar.progress.toLong())
            handler!!.postDelayed(this, 1000)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!mediaPlayer.isPlaying) {
                    container.callOnClick()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS ->
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying) {
                    container.callOnClick()
                }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                /*
                 * Lost focus for a short time, but we have to stop
                 * playback. We don't release the media player because playback
                 * is likely to resume
                 */
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> if (mediaPlayer.isPlaying) {
                mediaPlayer.setVolume(.1f, .1f)
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val value: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            value = audioManager!!.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("deprecation") // This one's deprecated, other one won't work
            value = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
        }

        return value == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun removeAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager!!.abandonAudioFocusRequest(focusRequest!!)
            } else {
                @Suppress("Deprecation")
                audioManager!!.abandonAudioFocus(this)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            container.callOnClick()
        }
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        requireContext().registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private fun callStateListener() {
        val telephonyManager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> {
                        if (mediaPlayer.isPlaying) {
                            container.callOnClick()
                        }
                        ongoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        ongoingCall = false
                        /* no-op */
                        // Wait for user action or dismiss
                    }
                }
            }
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun getRawData() {
        launch {

            var metadata: MetadataHelper.AudioMetadata
            val songInfo: String

            withContext(Dispatchers.Default) {
                metadata = MetadataHelper.getAudioMetadata(context!!, songUri!!)
                songInfo = "${getBitrate(context!!, songUri!!)} • ${getSampling(context!!, songUri!!)} • ${getFileExtension(context!!, songUri!!)}"
            }

            title.text = metadata.title
            artist.text = metadata.artists
            album.text = metadata.album
            info.text = songInfo

            title.isSelected = true
            artist.isSelected = true
            album.isSelected = true
            info.isSelected = true

            mimeImageView.loadFromFileDescriptor(requireContext(), songUri!!)
        }
    }

    private fun setSeekbarProgress(seekbarProgress: Int) {
        animation = ObjectAnimator.ofInt(seekBar, "progress", seekbarProgress)
        animation!!.duration = 1000 // 0.5 second
        animation!!.interpolator = LinearInterpolator()
        animation!!.start()
    }

    companion object {
        fun newInstance(uri: String): FileAssociationPlayer {
            val args = Bundle()
            val fragment = FileAssociationPlayer()
            fragment.arguments = args
            args.putString("file_content_uri", uri)
            return fragment
        }
    }
}
