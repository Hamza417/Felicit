package app.simple.felicit.medialoader.mediamodels

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
class AudioContent : Parcelable {
    @ColumnInfo(name = "name")
    lateinit var name: String

    @ColumnInfo(name = "title")
    lateinit var title: String

    @ColumnInfo(name = "path")
    lateinit var filePath: String

    @ColumnInfo(name = "artists")
    lateinit var artist: String

    @ColumnInfo(name = "album")
    var album: String = ""

    @ColumnInfo(name = "artUri")
    lateinit var artUri: String

    @ColumnInfo(name = "file_size")
    var musicSize: Int = 0

    @ColumnInfo(name = "duration")
    var duration: Long = 0

    @PrimaryKey
    @ColumnInfo(name = "id")
    var musicID: Long = 0

    @ColumnInfo(name = "file_uri")
    lateinit var fileStringUri: String

    @ColumnInfo(name = "date_added")
    var dateAdded: Long = 0

    @ColumnInfo(name = "date_modified")
    var dateModified: Long = 0

    @ColumnInfo(name = "date_taken")
    var dateTaken: Long = 0

    @ColumnInfo(name = "track")
    var track: Int = 0

    @ColumnInfo(name = "mime_type")
    lateinit var mimeType: String

    @ColumnInfo (name = "disc_number")
    var discNumber = 0

    @ColumnInfo (name = "year")
    var year = 0

    @ColumnInfo (name = "album_id")
    var albumId = 0L
}