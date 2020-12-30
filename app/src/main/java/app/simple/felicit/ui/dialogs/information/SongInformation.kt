package app.simple.felicit.ui.dialogs.information

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import app.simple.felicit.R
import app.simple.felicit.decoration.views.CustomBottomSheetDialog
import app.simple.felicit.medialoader.mediamodels.AudioContent
import app.simple.felicit.util.AudioHelper.getSampling
import app.simple.felicit.util.AudioHelper.toBitrate
import app.simple.felicit.util.FileUtils.getFileSize
import app.simple.felicit.util.NumberHelper.getFormattedTime
import app.simple.felicit.util.UriHelper
import app.simple.felicit.util.UriHelper.asUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class SongInformation : CustomBottomSheetDialog() {

    fun newInstance(audioContent: AudioContent): SongInformation {
        val args = Bundle()
        val fragment = SongInformation()
        fragment.arguments = args
        args.putParcelable("song_information", audioContent)
        return fragment
    }

    private lateinit var audioContent: AudioContent

    private lateinit var title: TextView
    private lateinit var artist: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var size: TextView
    private lateinit var format: TextView
    private lateinit var bitrate: TextView
    private lateinit var sampling: TextView
    private lateinit var path: TextView
    private lateinit var addedOn: TextView
    private lateinit var genre: TextView
    private lateinit var duration: TextView
    private lateinit var disc: TextView
    private lateinit var track: TextView
    private lateinit var albumArtist: TextView
    private lateinit var composer: TextView

    private lateinit var search: ImageButton
    private lateinit var lyrics: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_file_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioContent = arguments!!.getParcelable("song_information")!!

        title = view.findViewById(R.id.info_title)
        artist = view.findViewById(R.id.info_artist)
        album = view.findViewById(R.id.info_album)
        year = view.findViewById(R.id.info_year)
        size = view.findViewById(R.id.info_size)
        format = view.findViewById(R.id.info_format)
        bitrate = view.findViewById(R.id.info_bitrate)
        sampling = view.findViewById(R.id.info_sampling)
        path = view.findViewById(R.id.info_path)
        addedOn = view.findViewById(R.id.info_date_modified)
        genre = view.findViewById(R.id.info_genre)
        duration = view.findViewById(R.id.info_duration)
        disc = view.findViewById(R.id.info_disc)
        track = view.findViewById(R.id.info_track)
        albumArtist = view.findViewById(R.id.info_album_artist)
        composer = view.findViewById(R.id.info_composer)
        search = view.findViewById(R.id.info_search)
        lyrics = view.findViewById(R.id.info_lyrics)

        title.text = audioContent.title
        artist.text = audioContent.artist
        album.text = audioContent.album
        year.text = audioContent.year.toString()
        size.text = getFileSize(audioContent.musicSize.toDouble())
        format.text = UriHelper.getFileExtension(requireContext(), audioContent.fileStringUri.asUri())
        path.text = audioContent.filePath
        addedOn.text = DateFormat.format("EEEE, dd MMMM yyyy, hh:mm a", Date(audioContent.dateAdded * 1000))

        getRawData()

        search.setOnClickListener {
            try {
                val uri = Uri.parse("http://www.google.com/#q=" + URLEncoder.encode(audioContent.artist + " - " + audioContent.title, "UTF-8"))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: UnsupportedEncodingException) {
                Toast.makeText(context, "Error: Unsupported Text Format", Toast.LENGTH_SHORT).show()
            }
        }

        lyrics.setOnClickListener {
            try {
                val uri = Uri.parse("http://www.google.com/#q=" + URLEncoder.encode(audioContent.artist + " - " + audioContent.title + " Lyrics", "UTF-8"))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: UnsupportedEncodingException) {
                Toast.makeText(context, "Error: Unsupported Text Format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRawData() {
        CoroutineScope(Dispatchers.Default).launch {

            var duration = ""
            var genre = ""
            var bitrate = ""
            var disc = ""
            var track = ""
            var albumArtist = ""
            var composer = ""
            var sample = ""

            try {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(requireContext(), audioContent.fileStringUri.asUri())

                duration = getFormattedTime(audioContent.duration)
                println(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))
                bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt()!!.toBitrate()

                try {
                    genre = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)!!
                } catch (ignored: NullPointerException) {
                }
                try {
                    disc = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)!!
                } catch (ignored: NullPointerException) {
                }
                try {
                    track = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)!!
                } catch (ignored: NullPointerException) {
                }
                try {
                    albumArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)!!
                } catch (ignored: NullPointerException) {
                }
                try {
                    composer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)!!
                } catch (ignored: NullPointerException) {
                }
                sample = getSampling(requireContext(), audioContent.fileStringUri.asUri(), audioContent.filePath)

                mediaMetadataRetriever.close()
            } catch (ignored: IllegalArgumentException) {
            } catch (ignored: NoSuchMethodError) {
            }

            withContext(Dispatchers.Main) {
                this@SongInformation.duration.text = duration
                this@SongInformation.genre.text = genre
                this@SongInformation.bitrate.text = bitrate
                this@SongInformation.disc.text = disc
                this@SongInformation.track.text = track
                this@SongInformation.albumArtist.text = albumArtist
                this@SongInformation.composer.text = composer
                this@SongInformation.sampling.text = sample
            }
        }
    }
}