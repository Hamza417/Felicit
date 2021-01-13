package app.simple.felicit.helper

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object UriHelper {
    /**
     *  Get raw path of the file from content URI
     *
     *  @param context
     *  @param uri
     *  @return path as string
     */
    fun getPathFromURI(context: Context, uri: Uri): String {

        var realPath = String()

        uri.path?.let { path ->

            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?

            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                databaseUri = uri
                selection = null
                selectionArgs = null
            }

            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = context.contentResolver.query(
                    databaseUri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        realPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }

                    cursor.close()
                }
            } catch (ignored: Exception) {
            }
        }
        return realPath
    }

    /**
     * Get extension of any Mime Type based content URI
     */
    fun getFileExtension(context: Context, uri: Uri?): String {
        return "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri!!))
    }

    /**
     * Quickly formats the provided string into a
     * valid URI
     *
     * @return URI
     */
    fun String.asUri(): Uri {
        return Uri.parse(this)
    }
}