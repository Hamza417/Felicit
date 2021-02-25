package app.simple.felicit.ui.dialogs.action

import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import app.simple.felicit.R
import app.simple.felicit.decoration.knob.RotaryKnobListener
import app.simple.felicit.decoration.knob.RotaryKnobView
import app.simple.felicit.decoration.views.CustomDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolumePanel : DialogFragment(), RotaryKnobListener {

    private lateinit var volumeKnob: RotaryKnobView
    private lateinit var volumeTextView: TextView
    private lateinit var audioManager: AudioManager
    private lateinit var valueAnimator: ValueAnimator

    private var maxSupportedSystemVolume: Int = 0

    fun newInstance(): VolumePanel {
        val args = Bundle()
        val fragment = VolumePanel()
        fragment.arguments = args
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog!!.window ?: return
        window.attributes.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_volume_panel, container, false)

        volumeTextView = view.findViewById(R.id.current_volume)
        volumeKnob = view.findViewById(R.id.volume_knob)

        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxSupportedSystemVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeKnob.listener = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnKeyListener { dialog, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                }
                KeyEvent.KEYCODE_BACK -> {
                    dialog.dismiss()
                }
            }

            val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100.0f / (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 1.0f)
            updateVolume(volumeKnob.value.toFloat(), volume)
            true
        }
    }

    override fun onRotate(value: Int) {
        volumeTextView.text = StringBuilder().append(value).append("%")

        /**
         * Setting volume in a background thread is a workaround to solve
         * lags in some phone when volume is deliberately changed programmatically
         */
        CoroutineScope(Dispatchers.Default).launch {
            val volume = ((value / 100.0f) * maxSupportedSystemVolume).toInt()
            if (volume != audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            }
        }
    }

    override fun onKnobTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {

            }
        }
    }

    private fun updateVolume(currentVolume: Float, updatedVolume: Float) {
        valueAnimator = ValueAnimator.ofFloat(currentVolume, updatedVolume)
        valueAnimator.duration = 1000L
        valueAnimator.addUpdateListener { animation -> volumeKnob.setCurrentPosition(animation.animatedValue as Float) }
        valueAnimator.start()
    }
}