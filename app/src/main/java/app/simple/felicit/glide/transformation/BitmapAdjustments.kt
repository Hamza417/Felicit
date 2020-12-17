package app.simple.felicit.glide.transformation

import android.content.Context
import android.graphics.Bitmap
import app.simple.felicit.util.getRoundedCornerBitmap

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest


class BitmapAdjustments(private val context: Context) : BitmapTransformation() {
    public override fun transform(pool: BitmapPool, bitmapToTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return getRoundedCornerBitmap(bitmapToTransform, 0.2f)
    }

    override fun equals(other: Any?): Boolean {
        return other is BitmapAdjustments
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val ID = "app.simple.felicit.glide.transformation.BitmapAdjustments"
        private val ID_BYTES: ByteArray = ID.toByteArray(Charset.forName("UTF-8"))
    }
}