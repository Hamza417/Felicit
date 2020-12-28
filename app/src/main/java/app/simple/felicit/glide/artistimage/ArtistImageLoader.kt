package app.simple.felicit.glide.artistimage

import android.content.Context
import app.simple.felicit.network.deezer.DeezerRestService
import app.simple.felicit.network.deezer.DeezerRestService.Companion.createDefaultOkHttpClient
import app.simple.felicit.network.deezer.DeezerRestService.Companion.invoke
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ArtistImageLoader(private val deezerApiService: DeezerRestService, private val okhttp: OkHttpClient) : ModelLoader<ArtistImage, InputStream> {
    override fun buildLoadData(model: ArtistImage, width: Int, height: Int,
                               options: Options): LoadData<InputStream> {
        return LoadData(ObjectKey(model.artistName), ArtistImageFetcher(deezerApiService, okhttp, model))
    }

    override fun handles(model: ArtistImage): Boolean {
        return true
    }

    class Factory(context: Context) : ModelLoaderFactory<ArtistImage, InputStream> {
        private val deezerApiClient: DeezerRestService = invoke(
            createDefaultOkHttpClient(context)
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        )
        private val okHttp: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .build()

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ArtistImage, InputStream> {
            return ArtistImageLoader(deezerApiClient, okHttp)
        }

        override fun teardown() {}

    }

    companion object {
        // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
        private const val TIMEOUT = 2000
    }
}