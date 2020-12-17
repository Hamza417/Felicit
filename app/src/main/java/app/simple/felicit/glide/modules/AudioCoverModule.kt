package app.simple.felicit.glide.modules

import android.content.Context
import android.graphics.Bitmap
import app.simple.felicit.R
import app.simple.felicit.glide.artistimage.ArtistImage
import app.simple.felicit.glide.artistimage.ArtistImageLoader
import app.simple.felicit.glide.filedescriptorcover.DescriptorCoverLoader
import app.simple.felicit.glide.filedescriptorcover.DescriptorCoverModel
import app.simple.felicit.glide.pathcover.AudioCoverLoader
import app.simple.felicit.glide.pathcover.AudioCoverModel
import app.simple.felicit.glide.transformation.BitmapAdjustments
import app.simple.felicit.glide.uricover.UriCoverLoader
import app.simple.felicit.glide.uricover.UriCoverModel
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream

@GlideModule
class AudioCoverModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultTransitionOptions(Bitmap::class.java, BitmapTransitionOptions.withCrossFade())
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fallback(R.drawable.ic_icon)
                .error(R.drawable.ic_icon)
                .dontTransform()
                .transform(BitmapAdjustments(context))
        )
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(AudioCoverModel::class.java, InputStream::class.java, AudioCoverLoader.Factory())
        registry.append(ArtistImage::class.java, InputStream::class.java, ArtistImageLoader.Factory(context))
        registry.append(UriCoverModel::class.java, InputStream::class.java, UriCoverLoader.Factory())
        registry.append(DescriptorCoverModel::class.java, InputStream::class.java, DescriptorCoverLoader.Factory())
    }
}