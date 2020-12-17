package app.simple.felicit.decoration.coloredshadow

import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.*

object BlurShadow {

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    fun blur(bitmap: Bitmap, radius: Float): Bitmap {
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(bitmap)
        return bitmap
    }

    fun getShadowBitmap(bitmap: Bitmap, isTranslucent: Boolean, radius: Float): Bitmap {
        val allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
        val allocationOut = Allocation.createTyped(renderScript, allocationIn.type)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val colorMatrixScript = ScriptIntrinsicColorMatrix.create(renderScript, Element.U8_4(renderScript))

        val matrix = if (isTranslucent) {
            Matrix4f(floatArrayOf(
                0.4f, 0f, 0f, 0f,
                0f, 0.4f, 0f, 0f,
                0f, 0f, 0.4f, 0f,
                0f, 0f, 0f, 0.6f))
        } else {
            Matrix4f(floatArrayOf(
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0.4f))
        }

        colorMatrixScript.setColorMatrix(matrix)
        colorMatrixScript.forEach(allocationIn, allocationOut)

        blurScript.setRadius(radius)
        blurScript.setInput(allocationOut)
        blurScript.forEach(allocationIn)

        allocationIn.copyTo(bitmap)
        allocationIn.destroy()
        allocationOut.destroy()

        return bitmap
    }
}
