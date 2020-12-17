package app.simple.felicit.glide.pathcover

import kotlin.math.abs

class AudioCoverModel(var mediaPath: String) {
    @Throws(NullPointerException::class)
    override fun hashCode(): Int {
        try {
            return abs(mediaPath.toByteArray().size + mediaPath.hashCode())
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other.javaClass != this.javaClass) {
            return false
        }
        val compare = other as AudioCoverModel
        try {
            return compare.mediaPath == mediaPath && compare.mediaPath.toByteArray().size == mediaPath.toByteArray().size
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}