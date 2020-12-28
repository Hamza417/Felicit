package app.simple.felicit.glide.modules

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import app.simple.felicit.glide.artistimage.ArtistImage
import app.simple.felicit.glide.filedescriptorcover.DescriptorCoverModel
import app.simple.felicit.glide.pathcover.AudioCoverModel
import app.simple.felicit.glide.uricover.UriCoverModel

object AudioCoverUtil {
    /**
     * @param uri requires a valid art uri
     *
     * Asynchronously load Album Arts for song files from their URIs
     */
    fun ImageView.loadFromUri(context: Context, uri: Uri) {
        GlideApp.with(this).asBitmap().load(UriCoverModel(context, uri)).into(this)
    }

    /**
     * @param uri requires a valid file uri and not art uri else
     * error 0x80000000 will be thrown by the MediaMetadataRetriever
     *
     * Asynchronously load Album Arts for song files from their URIs using file descriptor
     */
    fun ImageView.loadFromFileDescriptor(context: Context, uri: Uri) {
        GlideApp.with(this).asBitmap().load(DescriptorCoverModel(context, uri)).into(this)
    }

    /**
     * @param uri requires a valid file uri and not art uri else
     * error 0x80000000 will be thrown by the MediaMetadataRetriever
     *
     * Asynchronously load Album Arts for song files from their URIs using file descriptor
     * intended to be used for ViewPagers as it does not modify any of the bitmaps
     * like rounded corners
     */
    fun ImageView.loadFromFileDescriptorForPagers(context: Context, uri: Uri) {
        GlideApp.with(context).asBitmap().load(DescriptorCoverModel(context, uri)).dontTransform().into(this)
    }

    /**
     * Asynchronously load Album Arts for song files from their raw paths
     */
    fun ImageView.loadFromPath(path: String) {
        GlideApp.with(this).asBitmap().load(AudioCoverModel(path)).into(this)
    }

    /**
     * @param artistName is the name of the artist, possibly a single name but a band name could
     * be suffice as well
     *
     * Asynchronously load artist image from Deezer using Deezer API
     * Requires a working Internet connection
     */
    fun ImageView.loadArtistImage(artistName: String) {
        GlideApp.with(this).asBitmap().load(ArtistImage(artistName, false)).into(this)
    }
}