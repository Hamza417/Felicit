package app.simple.felicit.glide.modules

import android.widget.ImageView
import app.simple.felicit.R
import app.simple.felicit.glide.artistimage.ArtistImage
import app.simple.felicit.glide.pathcover.AudioCoverModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object AudioCoverPagers {
    /**
     * @param path is the raw path of the original file
     * Asynchronously load image from path provided
     */
    fun ImageView.loadCoverForPagerFromFile(path: String) {
        Glide.with(this)
            .asBitmap()
            .load(AudioCoverModel(path))
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(this)
    }

    /**
     * @param uri is the content art uri of the original file
     * Asynchronously load image from path provided
     */
    fun ImageView.loadCoverForPagerFromUri(uri: String) {
        Glide.with(this)
            .asBitmap()
            .load(AudioCoverModel(uri))
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(this)
    }

    /**
     * This function loads the artist images from the internet
     * and is intended to be used for full screen or view pager
     *
     * for normal cover image use [AudioCoverUtil.loadArtistImage] function
     */
    fun ImageView.loadArtistImageForScreen(name: String) {
        Glide.with(this)
            .asBitmap()
            .load(ArtistImage(name, false))
            .centerCrop()
            .fallback(R.drawable.ic_icon)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }
}