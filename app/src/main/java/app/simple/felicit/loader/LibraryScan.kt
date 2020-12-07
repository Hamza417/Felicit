package app.simple.felicit.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.room.Room
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.model.Song
import app.simple.felicit.util.getFileExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Deprecated(
    message = "This library takes a lot of time and is not feasible for production use",
    replaceWith = ReplaceWith("MediaLoader"))
@SuppressWarnings("NewApi")
class LibraryScan {
    fun scanSongList(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val songDatabase =
                Room.databaseBuilder(context, SongDatabase::class.java, "songs.db").build()

            val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
            val genresProjection =
                arrayOf(MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID)
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val musicCursor = context.contentResolver.query(musicUri, null, selection, null, null)

            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val albumId = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)
                val songPath = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val songYear = musicCursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
                val durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val dateColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val songDisplayName =
                    musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)

                //add songs to list
                do {
                    val id = musicCursor.getLong(idColumn)
                    val thisAlbumId = musicCursor.getLong(albumId)
                    val title = musicCursor.getString(titleColumn)
                    val artist = musicCursor.getString(artistColumn)
                    val album = musicCursor.getString(albumColumn)
                    val contentUri = musicCursor.getString(songPath)
                    val year = musicCursor.getString(songYear)
                    val displayName = musicCursor.getString(songDisplayName)
                    val date = musicCursor.getLong(dateColumn)
                    val duration = musicCursor.getString(durationColumn)

                    var genre = "unknown"

                    try {
                        val uri = MediaStore.Audio.Genres.getContentUriForAudioId(
                            "external",
                            id.toInt()
                        )
                        val genreCursor = context.contentResolver.query(
                            uri,
                            genresProjection,
                            null,
                            null,
                            null
                        )
                        var genreIndex: Int
                        if (genreCursor != null) {
                            genreIndex =
                                genreCursor.getColumnIndex(MediaStore.Audio.Genres.NAME)
                            if (genreCursor.moveToFirst()) {
                                do {
                                    genre = genreCursor.getString(genreIndex)
                                } while (genreCursor.moveToNext())
                            }
                            genreCursor.close()
                        }
                    } catch (e: java.lang.IllegalArgumentException) {
                        e.printStackTrace()
                        genre = "unknown"
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        genre = "unknown"
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                        genre = "unknown"
                    }

                    val f = File(contentUri)

                    if (f.exists()) {
                        songDatabase.songDao()?.insertSong(
                            Song(
                                id.toInt(),
                                thisAlbumId.toInt(),
                                title,
                                artist,
                                album,
                                contentUri,
                                getFileExtension(context = context, Uri.parse(contentUri)),
                                date,
                                year,
                                genre,
                                contentUri.replace(displayName, ""),
                                duration
                            )
                        )
                    }
                } while (musicCursor.moveToNext())

                musicCursor.close()
            }
        }
    }
}