package app.simple.felicit.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import app.simple.felicit.R
import java.io.FileNotFoundException
import java.io.InputStream

object ImageHelper {
    /**
     * Converts image uri to bitmap
     *
     * Recommended to run inside a background thread
     */
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: FileNotFoundException) {
            null
        }
    }

    /**
     * Converts image uri in string format to bitmap
     *
     * Should be used only for MediaStyle notifications
     */
    fun getBitmapFromUriForNotifications(context: Context, uri: String): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(uri))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: FileNotFoundException) {
            R.drawable.ic_icon.toBitmap(context)
        }
    }

    /**
     * Converts vector drawable to bitmap
     *
     * Use as <code>vectorResId.toBitmap(context)</code>
     */
    @JvmStatic
    fun Int.toBitmap(context: Context): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, this)
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    /**
     * @param bitmap takes the bitmap value and makes the corners rounded
     * by [bitmap] * 0.2f
     *
     * TODO - add shadow to bitmap
     */
    fun getRoundedCornerBitmap(bitmap: Bitmap, @FloatRange(from = 0.1, to = 0.5) radius: Float, padding: Int): Bitmap {
        val paddedWidth = 0.coerceAtLeast(bitmap.width - (padding * 2))
        val paddedHeight = 0.coerceAtLeast(bitmap.height - (padding * 2))

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        //val rect = Rect(padding, padding, paddedWidth + padding, paddedHeight + padding)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val roundPx = bitmap.height * radius

        //paint.setShadowLayer(1f, 0f, 5f, Color.DKGRAY)
        //val shadowPaint = Paint()
        //shadowPaint.color = Color.DKGRAY
        //shadowPaint.maskFilter = BlurMaskFilter(padding * 0.5f, BlurMaskFilter.Blur.NORMAL)
        //canvas.drawRoundRect(RectF(rect), roundPx, roundPx, shadowPaint)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(RectF(rect), roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun generateShadowBitmap(bitmap: Bitmap?, cornerRadius: Float?, elevation: Float): Bitmap {
        val shape = RectF(0f, 0f,bitmap?.width?.toFloat() as Float, bitmap.height.toFloat())
        val paint = Paint()
        val shadowBitmap = Bitmap.createBitmap((bitmap.width * 2), (bitmap.height * 2), Bitmap.Config.ARGB_8888)
        val cv = Canvas(shadowBitmap)

        paint.maskFilter = BlurMaskFilter(elevation * 0.5f, BlurMaskFilter.Blur.NORMAL)

        cv.translate(
            shape.width() / 2,
            shape.height() / 2
        )
        cv.drawRoundRect(
            shape,
            cornerRadius ?: 0f,
            cornerRadius ?: 0f,
            paint
        )

        return shadowBitmap
    }
}