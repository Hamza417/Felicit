package app.simple.felicit.glide.uricover

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.FileNotFoundException
import java.io.InputStream

class UriCoverFetcher internal constructor(private val uriCoverModel: UriCoverModel) : DataFetcher<InputStream> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        try {
            val inputStream: InputStream? = uriCoverModel.context.contentResolver.openInputStream(uriCoverModel.artUri)
            callback.onDataReady(inputStream)
            inputStream?.close()
        } catch (e: IllegalArgumentException) {
            callback.onLoadFailed(e)
        } catch (e: FileNotFoundException) {
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() {
        // Cleared
    }

    override fun cancel() {
        // Probably already cleared
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}