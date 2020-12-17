package app.simple.felicit.medialoader

import android.content.Context

object MediaLoader {
    /**
     * Returns a static instance of [VideoGet]
     */
    fun withVideoContext(context: Context?): VideoGet {
        return VideoGet.getInstance(context)
    }

    /**
     * Returns a static instance of [PictureGet]
     */
    fun withPictureContext(context: Context?): PictureGet {
        return PictureGet.getInstance(context)
    }

    /**
     * Returns a static instance of [AudioGet]
     */
    fun withAudioContext(context: Context?): AudioGet? {
        return context?.let { AudioGet.getInstance(it) }
    }
}