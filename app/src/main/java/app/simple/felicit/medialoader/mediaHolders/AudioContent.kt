package app.simple.felicit.medialoader.mediaHolders

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "songs")
class AudioContent : Parcelable{
    @ColumnInfo(name = "name")
    lateinit var name: String

    @ColumnInfo(name = "title")
    lateinit var title: String

    @ColumnInfo(name = "path")
    lateinit var filePath: String

    @ColumnInfo(name = "artists")
    lateinit var artist: String

    @ColumnInfo(name = "album")
    lateinit var album: String

    @ColumnInfo(name = "artUri")
    lateinit var artUri: String

    @ColumnInfo(name = "file_size")
    var musicSize: Long = 0

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
}