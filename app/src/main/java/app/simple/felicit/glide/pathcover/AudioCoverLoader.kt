package app.simple.felicit.glide.pathcover

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

class AudioCoverLoader : ModelLoader<AudioCoverModel, InputStream> {
    override fun buildLoadData(AudioCoverModel: AudioCoverModel, width: Int, height: Int, options: Options): LoadData<InputStream> {
        return LoadData(ObjectKey(AudioCoverModel), AudioCoverFetcher(AudioCoverModel))
    }

    fun getResourceFetcher(model: AudioCoverModel?, width: Int, height: Int): DataFetcher<InputStream> {
        return AudioCoverFetcher(model!!)
    }

    override fun handles(AudioCoverModel: AudioCoverModel): Boolean {
        return true
    }

    internal class Factory : ModelLoaderFactory<AudioCoverModel, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AudioCoverModel, InputStream> {
            return AudioCoverLoader()
        }

        override fun teardown() {}
    }
}