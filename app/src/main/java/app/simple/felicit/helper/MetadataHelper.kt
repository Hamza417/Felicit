package app.simple.felicit.helper

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import app.simple.felicit.R

object MetadataHelper {

    fun getAudioMetadata(context: Context, songUri: Uri): AudioMetadata {
        val audioMetadata = AudioMetadata()
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(context, songUri)

        audioMetadata.title = getSongTitleMeta(context, mediaMetadataRetriever)
        audioMetadata.artists = getSongArtistMeta(context, mediaMetadataRetriever)
        audioMetadata.album = getSongAlbumMeta(context, mediaMetadataRetriever)

        /**
         * Close the retriever instance to prevent
         * resource leaks
         */
        mediaMetadataRetriever.close()

        return audioMetadata
    }

    /**
     * Extracts the title metadata from the source
     * audio file
     *
     * @param context
     * @param mediaMetadataRetriever
     * @return [String]
     */
    private fun getSongTitleMeta(context: Context, mediaMetadataRetriever: MediaMetadataRetriever): String {
        return try {
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!!
        } catch (e: NullPointerException) {
            context.getString(R.string.unknown)
        }
    }

    /**
     * Extracts the artist metadata from the source
     * audio file
     *
     * @param context
     * @param mediaMetadataRetriever
     * @return [String]
     */
    private fun getSongArtistMeta(context: Context, mediaMetadataRetriever: MediaMetadataRetriever): String {
        return try {
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!!
        } catch (e: NullPointerException) {
            context.getString(R.string.unknown)
        }
    }

    /**
     * Extracts the album metadata from the source
     * audio file
     *
     * @param context
     * @param mediaMetadataRetriever
     * @return [String]
     */
    private fun getSongAlbumMeta(context: Context, mediaMetadataRetriever: MediaMetadataRetriever): String {
        return try {
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)!!
        } catch (e: NullPointerException) {
            context.getString(R.string.unknown)
        }
    }

    open class AudioMetadata {
        /**
         * Title of the audio file
         */
        var title = ""

        /**
         * Artist of the audio file
         */
        var artists = ""

        /**
         * Album of the audio file
         */
        var album = ""
    }
}
