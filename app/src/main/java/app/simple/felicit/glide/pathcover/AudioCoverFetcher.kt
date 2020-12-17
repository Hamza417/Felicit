package app.simple.felicit.glide.pathcover

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.*

class AudioCoverFetcher internal constructor(private val model: AudioCoverModel) : DataFetcher<InputStream> {
    private var stream: FileInputStream? = null
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(model.mediaPath)
            val picture = retriever.embeddedPicture
            if (null != picture) {
                callback.onDataReady(ByteArrayInputStream(picture))
            } else {
                callback.onDataReady(fallback(model.mediaPath))
            }
        } catch (ignored: IllegalArgumentException) {
        } finally {
            retriever.release()
        }
    }

    override fun cleanup() {
        try {
            if (null != stream) {
                stream!!.close()
            }
        } catch (ignore: IOException) {
        }
    }

    override fun cancel() {
        // cannot cancel
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

    private fun fallback(path: String): InputStream? {
        val parent = File(path).parentFile
        for (fallback in FALLBACKS) {
            // TODO make it smarter by enumerating folder contents and filtering for files
            // example algorithm for that: http://askubuntu.com/questions/123612/how-do-i-set-album-artwork
            val cover = File(parent, fallback)
            if (cover.exists()) {
                try {
                    return FileInputStream(cover).also { stream = it }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    companion object {
        val FALLBACKS = arrayOf("cover.jpg", "album.jpg", "folder.jpg")
    }
}