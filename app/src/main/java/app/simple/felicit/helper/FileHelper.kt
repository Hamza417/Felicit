package app.simple.felicit.helper

import java.text.DecimalFormat

object FileHelper {

    private val format = DecimalFormat("#.##")
    private const val KB: Long = 1024
    private const val MB = (KB * 1024)
    private const val GB = (MB * 1024)

    /**
     * @throws StringIndexOutOfBoundsException if invalid path is supplied
     * @return file extension
     * @param path takes the raw path string that may contains forward
     * slashes and filters out/returns the extension of the file
     * @see UriHelper.getFileExtension for getting file extension from URI
     */
    fun getFormat(path: String): String {
        try {
            val i = path.lastIndexOf('.')
            val p = path.lastIndexOf('/').coerceAtLeast(path.lastIndexOf('\\'))
            if (i > p) {
                return path.substring(i + 1)
            }
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            return "N/A"
        }

        return "N/A"
    }

    /**
     * Formats the file size into B, KB, MB or GB depending
     * on the parameter supplied
     *
     * @return formatted file size like 1024 to 1 KiB
     * @param size is size of the file in decimal format
     */
    fun getFileSize(size: Double): String {
        return when {
            size > MB -> {
                format.format(size / MB) + " MB"
            }
            size > KB -> {
                format.format(size / KB) + " KB"
            }
            size > GB -> {
                format.format(size / GB) + " GB"
            }
            else -> format.format(size) + " B"
        }
    }
}