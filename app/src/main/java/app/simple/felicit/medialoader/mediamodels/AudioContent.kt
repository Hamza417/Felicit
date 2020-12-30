package app.simple.felicit.medialoader.mediamodels

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
class AudioContent : Parcelable {
    @IgnoredOnParcel
    @ColumnInfo(name = "name")
    var name: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "title")
    var title: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "path")
    var filePath: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "artists")
    var artist: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "album")
    var album: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "artUri")
    var artUri: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "file_size")
    var musicSize: Int = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "duration")
    var duration: Long = 0

    @IgnoredOnParcel
    @PrimaryKey
    @ColumnInfo(name = "id")
    var musicID: Long = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "file_uri")
    var fileStringUri: String = ""

    @IgnoredOnParcel
    @ColumnInfo(name = "date_added")
    var dateAdded: Long = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "date_modified")
    var dateModified: Long = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "date_taken")
    var dateTaken: Long = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "track")
    var track: Int = 0

    @IgnoredOnParcel
    @ColumnInfo(name = "mime_type")
    var mimeType: String = ""

    @IgnoredOnParcel
    @ColumnInfo (name = "disc_number")
    var discNumber = 0

    @IgnoredOnParcel
    @ColumnInfo (name = "year")
    var year = 0

    @IgnoredOnParcel
    @ColumnInfo (name = "album_id")
    var albumId = 0L
}