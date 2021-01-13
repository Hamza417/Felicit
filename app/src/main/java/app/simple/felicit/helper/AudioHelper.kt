package app.simple.felicit.helper

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Looper
import app.simple.felicit.exceptions.NotABackgroundThreadException
import java.io.IOException

object AudioHelper {
    /**
     * Calculates the sampling of the given audio file in
     * kHz and returns the appended string
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws NullPointerException
     * @throws NotABackgroundThreadException
     * @param context a context of the calling region
     * @param fileUri Uri of the file
     * @param filePath Path of the file
     * @return Sampling of the given audio file as String
     */
    fun getSampling(context: Context, fileUri: Uri, filePath: String): String {
        if (Looper.myLooper() == Looper.getMainLooper())
            throw NotABackgroundThreadException()

        val mex = MediaExtractor()
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mex.setDataSource(context.contentResolver.openAssetFileDescriptor(fileUri, "r")!!)
            } else {
                mex.setDataSource(filePath)
            }
            return "${mex.getTrackFormat(0).getInteger(MediaFormat.KEY_SAMPLE_RATE).toFloat() / 1000} kHz"
        } catch (e: IOException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: NullPointerException) {
        } finally {
            mex.release()
        }

        return ""
    }

    /**
     * Calculates the bitrate of the given audio file in
     * kbps or mbps and returns the appended string
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws NullPointerException
     * @throws NotABackgroundThreadException
     * @param context a context of the calling region
     * @param fileUri Uri of the file
     * @return Bitrate of the given audio file as String
     */
    fun getBitrate(context: Context, fileUri: Uri): String {

        if(Looper.myLooper() == Looper.getMainLooper())
            throw NotABackgroundThreadException()

        val mediaMetadataRetriever = MediaMetadataRetriever()

        try {
            mediaMetadataRetriever.setDataSource(context, fileUri)
            return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)!!.toInt().toBitrate()
        } catch (e: IOException) {
        } catch (e: java.lang.IllegalArgumentException) {
        } catch (e: java.lang.NullPointerException) {
        } finally {
            mediaMetadataRetriever.release()
            mediaMetadataRetriever.close()
        }

        return ""
    }

    /**
     * This function is an extension function for [FileHelper.getBitrate]
     * and can be used independently with any [Int] value
     *
     * @see getBitrate
     */
    fun Int.toBitrate(): String {
        return when {
            this / 1000 < 1024 -> {
                "${this / 1000} kbit/s"
            }
            this / 1000 > 1024 -> {
                "${this / 1000} mbit/s"
            }
            else -> {
                "exceeding bitrate"
            }
        }
    }
}