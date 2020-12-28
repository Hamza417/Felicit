package app.simple.felicit.medialoader.mediamodels

import java.util.*

class AudioFolderContent {
    var musicFiles: ArrayList<AudioContent>
    var folderName: String? = null
    var folderPath: String? = null
    var bucketId = 0

    constructor() {
        musicFiles = ArrayList()
    }

    constructor(folderName: String?, folderPath: String?) {
        this.folderName = folderName
        this.folderPath = folderPath
        musicFiles = ArrayList()
    }

    val numberOfSongs: Int
        get() = musicFiles.size
}