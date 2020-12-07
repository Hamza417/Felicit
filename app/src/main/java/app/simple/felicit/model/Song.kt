package app.simple.felicit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "songs")
class Song : Serializable {
    @PrimaryKey
    var id: Long = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "artists")
    lateinit var artist: String

    @ColumnInfo(name = "album")
    lateinit var album: String

    @ColumnInfo(name = "path")
    lateinit var songPath: String

    @ColumnInfo(name = "format")
    var format: String? = null

    @ColumnInfo(name = "year")
    var year: String? = null

    @ColumnInfo(name = "date")
    var date: Long = 0

    @ColumnInfo(name = "album_id")
    var albumId = 0

    @ColumnInfo(name = "genre")
    var genre: String? = null

    @ColumnInfo(name = "folder")
    var folder: String? = null

    @ColumnInfo(name = "duration")
    lateinit var duration: String

    constructor(
            songID: Int,
            album_id: Int,
            songTitle: String?,
            songArtist: String,
            songAlbum: String,
            path: String,
            format: String?,
            date: Long,
            year: String?,
            genre: String?,
            folder: String?,
            duration: String
    ) {
        id = songID.toLong()
        this.albumId = album_id
        title = songTitle
        artist = songArtist
        album = songAlbum
        this.year = year
        songPath = path
        this.format = format
        this.date = date * 1000
        this.genre = genre
        this.folder = folder
        this.duration = duration
    }

    constructor()
}