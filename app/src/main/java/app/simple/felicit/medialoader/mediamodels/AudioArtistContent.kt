package app.simple.felicit.medialoader.mediamodels

import java.util.*

class AudioArtistContent {
    var artistName: String? = null
    var albums = ArrayList<AudioAlbumContent>()

    constructor() {}
    constructor(artistName: String?, albums: ArrayList<AudioAlbumContent>) {
        this.artistName = artistName
        this.albums = albums
    }

    val numOfSongs: Int
        get() {
            var numOfSongs = 0
            for (i in albums.indices) {
                numOfSongs += albums[i].numberOfSongs
            }
            return numOfSongs
        }
}