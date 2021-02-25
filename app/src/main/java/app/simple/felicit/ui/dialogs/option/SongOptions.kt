package app.simple.felicit.ui.dialogs.option

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ShareCompat
import app.simple.felicit.BuildConfig
import app.simple.felicit.R
import app.simple.felicit.decoration.views.CustomBottomSheetDialogFragment
import app.simple.felicit.glide.modules.AudioCoverUtil.loadFromUri
import app.simple.felicit.util.RingtoneUtils
import app.simple.felicit.models.AudioContent
import app.simple.felicit.ui.dialogs.information.SongInformation
import app.simple.felicit.helper.FileHelper.getFormat

class SongOptions : CustomBottomSheetDialogFragment() {

    fun newInstance(audioContent: AudioContent, source: String): SongOptions {
        val args = Bundle()
        val fragment = SongOptions()
        fragment.arguments = args
        args.putParcelable("song_meta", audioContent)
        args.putString("source", source)
        return fragment
    }

    private lateinit var audioContent: AudioContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_options_songs, container)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioContent = arguments?.getParcelable("song_meta")!!

        // Declaration
        val playNext = view.findViewById<LinearLayout>(R.id.play_next_options)
        val share = view.findViewById<LinearLayout>(R.id.share_option_songs)
        val favorite = view.findViewById<LinearLayout>(R.id.options_favorite_songs)
        val fileInformation = view.findViewById<LinearLayout>(R.id.file_information)
        val goToAlbum = view.findViewById<LinearLayout>(R.id.go_to_album)
        val goToArtist = view.findViewById<LinearLayout>(R.id.go_to_artist)
        val addToQueue = view.findViewById<LinearLayout>(R.id.song_options_add_to_queue)
        val addToPlaylist = view.findViewById<LinearLayout>(R.id.add_to_playlist)
        val ringtone = view.findViewById<LinearLayout>(R.id.options_ringtone_songs)
        val youtube = view.findViewById<LinearLayout>(R.id.go_to_youtube)
        val close = view.findViewById<ImageButton>(R.id.options_close_songs)
        val heart = view.findViewById<ImageView>(R.id.options_heart_songs)
        val ring = view.findViewById<ImageView>(R.id.options_ringtone_icon)
        val art = view.findViewById<ImageView>(R.id.options_art)
        val shareText = view.findViewById<TextView>(R.id.share_option_songs_options)
        val favoriteText = view.findViewById<TextView>(R.id.options_favorite_text_songs)
        val songTitle = view.findViewById<TextView>(R.id.song_title_options)
        val songArtist = view.findViewById<TextView>(R.id.song_artist_options)
        val ringtoneTv = view.findViewById<TextView>(R.id.options_ringtone_text_songs)

        if (arguments!!.getString("source") == "mime") {
            playNext.visibility = View.GONE
            addToQueue.visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.sub_category_container_one).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.sub_category_container_two).visibility = View.GONE
            this.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        if (ringtoneName == audioContent.title) {
            ring.setImageResource(R.drawable.ic_done)
            ringtone.isEnabled = false
            ringtoneTv.text = "Ringtone Set"
        }

        shareText.text = Html.fromHtml("Share • <font color=\"" + 0x999999 + "\">" + getFormat(audioContent.filePath))
        art.loadFromUri(requireContext(), Uri.parse(audioContent.artUri))
        songTitle.text = audioContent.title
        songArtist.text = audioContent.artist

        // Listeners
        close.setOnClickListener { this.dismiss() }

        share.setOnClickListener {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setStream(Uri.parse(audioContent.fileStringUri))
                .setType(audioContent.mimeType)
                .startChooser()
        }

        favorite.setOnClickListener { v: View? ->
            /* no-op */
        }

        ringtone.setOnClickListener {
            if (setRingtone()) {
                ring.setImageResource(R.drawable.ic_done)
                ringtoneTv.text = "Success"
            } else {
                ring.setImageResource(R.drawable.ic_error)
                ringtoneTv.text = "error!!"
            }
        }

        youtube.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SEARCH)
                if (BuildConfig.DEBUG) {
                    intent.setPackage("com.vanced.android.youtube")
                } else {
                    intent.setPackage("com.google.android.youtube")
                }
                intent.putExtra("query", audioContent.artist + " - " + audioContent.title)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "App does not exist", Toast.LENGTH_SHORT).show()
            }
        }

        fileInformation.setOnClickListener {
            SongInformation().newInstance(audioContent = audioContent).show(requireFragmentManager(), "song_information")
            dialog!!.dismiss()
        }
    }

    private fun setRingtone(): Boolean {
        return try {
            RingtoneUtils.setRingtone(requireContext(), ContentUris.withAppendedId(Uri.parse(audioContent.fileStringUri), audioContent.musicID))
        } catch (ignored: Throwable) {
            false
        }
    }

    private val ringtoneName: String
        get() {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(requireContext(), RingtoneManager.TYPE_RINGTONE)
            val defaultRingtone = RingtoneManager.getRingtone(activity, defaultRingtoneUri)
            return defaultRingtone.getTitle(requireContext())
        }

    companion object {
        enum class Source(val source: String) {
            MIME("mime"),
            APP("app")
        }
    }
}