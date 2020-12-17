package app.simple.felicit.glide.artistimage

import android.content.Context
import app.simple.felicit.network.deezer.DeezerResponse
import app.simple.felicit.network.deezer.DeezerRestService
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class ArtistImageFetcher internal constructor(private val context: Context, private val deezerApiService: DeezerRestService, private val okhttp: OkHttpClient, private val model: ArtistImage) : DataFetcher<InputStream> {
    @Volatile
    private var isCancelled = false
    private var call: Call<DeezerResponse>? = null
    private var streamFetcher: OkHttpStreamFetcher? = null

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        try {
            call = deezerApiService.getArtistImage(model.artistName)
            call!!.enqueue(object : Callback<DeezerResponse?> {
                override fun onResponse(call: Call<DeezerResponse?>, response: Response<DeezerResponse?>) {
                    if (isCancelled) {
                        callback.onDataReady(null)
                        return
                    }
                    try {
                        val deezerResponse = response.body()
                        val url = deezerResponse!!.data[0].pictureXl
                        streamFetcher = OkHttpStreamFetcher(okhttp, GlideUrl(url))
                        streamFetcher!!.loadData(priority, callback)
                    } catch (e: Exception) {
                        callback.onLoadFailed(Exception("No artist image url found"))
                    }
                }

                override fun onFailure(call: Call<DeezerResponse?>, throwable: Throwable) {
                    callback.onLoadFailed(Exception(throwable))
                }
            })
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() {
        if (streamFetcher != null) {
            streamFetcher!!.cleanup()
        }
    }

    override fun cancel() {
        isCancelled = true
        if (call != null) {
            call!!.cancel()
        }
        if (streamFetcher != null) {
            streamFetcher!!.cancel()
        }
    }
}