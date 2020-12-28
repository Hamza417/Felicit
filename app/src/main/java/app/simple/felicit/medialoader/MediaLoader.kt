package app.simple.felicit.medialoader

import android.content.Context

object MediaLoader {
    /**
     * Returns a static instance of [AudioGet]
     */
    fun withAudioContext(context: Context?): AudioGet? {
        return context?.let { AudioGet.getInstance(it) }
    }
}