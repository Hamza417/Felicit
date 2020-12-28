package app.simple.felicit.medialoader.mediamodels

import android.net.Uri
import java.util.*

class AudioAlbumContent {
    var albumName: String? = null
    var albumId: Long = 0
    var numberOfSongs = 0
    var albumArtUri: Uri? = null
    var albumArtist: String? = null
    var audioContents = ArrayList<AudioContent>()

    constructor(albumName: String?, albumId: Long, albumArtUri: Uri?, albumArtist: String?) {
        this.albumName = albumName
        this.albumId = albumId
        this.albumArtUri = albumArtUri
        this.albumArtist = albumArtist
    }

    fun addAudioContent(audioContent: AudioContent) {
        audioContents.add(audioContent)
    }

    fun addNumberOfSongs() {
        numberOfSongs++
    }
}