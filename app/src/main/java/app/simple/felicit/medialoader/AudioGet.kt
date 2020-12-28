package app.simple.felicit.medialoader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import app.simple.felicit.medialoader.mediamodels.AudioAlbumContent
import app.simple.felicit.medialoader.mediamodels.AudioArtistContent
import app.simple.felicit.medialoader.mediamodels.AudioContent
import app.simple.felicit.medialoader.mediamodels.GenreContent

/**
 * Due to horrible coding patterns of people at Google and whoever designed the MediaStore API
 * This region of the code is messed up, all the Media Columns will work up till API
 * I don't know what and not only to the API it warning about
 *
 * use @suppress NewApi to suppress all the warnings but make sure to check if all the columns are working
 * Currently [MediaStore.Audio.Media.GENRE] is the only one not working in API 29 and documentation says that
 * it is added only in API 30, currently I don't have the resources to verify this information
 */
@Suppress("deprecation", "newApi", "unused")
class AudioGet private constructor(context: Context) {
    private val audioContext: Context = context.applicationContext

    /**
     * Returns an Arraylist of [AudioContent]
     */
    fun getAllAudioContent(contentLocation: Uri): ArrayList<AudioContent> {
        val allAudioContent = ArrayList<AudioContent>()

        cursor = audioContext.contentResolver.query(
            contentLocation,
            audioProjection,
            selection,
            null,
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val audioContent = AudioContent()
                val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                audioContent.fileStringUri = Uri.withAppendedPath(contentLocation, audioContent.musicID.toString()).toString()
                audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                audioContent.dateTaken = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_TAKEN))
                audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), albumId.toString()).toString()
                audioContent.track = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.TRACK))
                audioContent.mimeType = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                audioContent.year = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR))

                //for android 10 exclusively
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Uri contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    try {
                        AssetFileDescriptor file = audioContext.getContentResolver().openAssetFileDescriptor(contentUri, "r");
                        audioContent.setMusicPathQ(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }*/

                allAudioContent.add(audioContent)
            } while (cursor!!.moveToNext())
            cursor!!.close()
        }

        //try saving in cache for better loading next time
        audioContext.externalCacheDir
        return allAudioContent
    }

    fun getAllGenre(contentLocation: Uri): ArrayList<GenreContent> {
        val genreContentList = ArrayList<GenreContent>()

        genresCursor = audioContext.contentResolver.query(
            contentLocation,
            genresProjection,
            null,
            null,
            "LOWER (" + MediaStore.Audio.Genres.NAME + ") ASC"
        )

        if (genresCursor != null && genresCursor!!.moveToFirst()) {
            do {
                val genreContent = GenreContent()
                genreContent.genreId = genresCursor!!.getInt(genresCursor!!.getColumnIndex(MediaStore.Audio.Genres._ID))
                genreContent.genreName = genresCursor!!.getString(genresCursor!!.getColumnIndex(MediaStore.Audio.Genres.NAME))
                genreContentList.add(genreContent)

            } while (genresCursor!!.moveToNext())

            genresCursor!!.close()
        }

        return genreContentList
    }

    fun getAllSongsForGenre() {
        val audioContent = AudioContent()

        val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", audioContent.musicID.toInt())

        genresCursor = audioContext.contentResolver.query(uri, genresProjection, null, null, null)
        val genreColumnIndex: Int = genresCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

        if (genresCursor!!.moveToFirst()) {
            //audioContent.genre = genresCursor!!.getString(genreColumnIndex)
            genresCursor!!.close()
        }
    }

    /**
     * Returns an ArrayList of [AudioAlbumContent]
     */
    fun getAllAlbums(contentLocation: Uri?): ArrayList<AudioAlbumContent> {
        val audioAlbumContents = ArrayList<AudioAlbumContent>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        cursor = audioContext.contentResolver.query(contentLocation!!, audioProjection, selection, null, "LOWER (" + MediaStore.Audio.Media.ALBUM + ") ASC")

        val albumNames = ArrayList<String>()

        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    var album: AudioAlbumContent
                    val audioContent = AudioContent()
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    audioContent.fileStringUri = Uri.withAppendedPath(contentLocation, audioContent.musicID.toString()).toString()
                    audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    audioContent.dateTaken = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_TAKEN))
                    audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), albumId.toString()).toString()
                    audioContent.track = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.TRACK))
                    audioContent.mimeType = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                    audioContent.year = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR))

                    if (!albumNames.contains(albumId.toString())) {
                        albumNames.add(albumId.toString())
                        album = AudioAlbumContent(audioContent.album, albumId, Uri.parse(audioContent.artUri), audioContent.artist)
                        album.addAudioContent(audioContent)
                        album.addNumberOfSongs()
                        audioAlbumContents.add(album)
                    } else {
                        for (i in audioAlbumContents.indices) {
                            if (audioAlbumContents[i].albumId.toString() == albumId.toString()) {
                                audioAlbumContents[i].addAudioContent(audioContent)
                                audioAlbumContents[i].addNumberOfSongs()
                            }
                        }
                    }
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        //try saving in cache for better loading next time
        audioContext.externalCacheDir
        return audioAlbumContents
    }

    /**
     * Returns an ArrayList of String representing artist ids in the [MediaStore]
     */
    private fun getAllAlbumsByArtistId(artist_id: String, contentLocation: Uri): ArrayList<AudioAlbumContent> {
        val allAlbums = ArrayList<AudioAlbumContent>()
        cursor = audioContext.contentResolver.query(
            contentLocation,
            audioProjection,
            MediaStore.Audio.Artists.ARTIST + " like ? ",
            arrayOf("%$artist_id%"),
            "LOWER (" + MediaStore.Audio.Artists.ARTIST + ") ASC"
        )

        val albumNames = ArrayList<String>()
        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                var album: AudioAlbumContent
                val audioContent = AudioContent()

                audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                audioContent.fileStringUri = Uri.withAppendedPath(contentLocation, audioContent.musicID.toString()).toString()
                audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                audioContent.dateTaken = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_TAKEN))
                audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), audioContent.albumId.toString()).toString()
                audioContent.track = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.TRACK))
                audioContent.mimeType = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                audioContent.year = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR))

                if (!albumNames.contains(audioContent.albumId.toString())) {
                    albumNames.add(audioContent.albumId.toString())
                    album = AudioAlbumContent(audioContent.album, audioContent.albumId, Uri.parse(audioContent.artUri), audioContent.artist)
                    album.addAudioContent(audioContent)
                    album.addNumberOfSongs()
                    allAlbums.add(album)
                } else {
                    for (i in allAlbums.indices) {
                        if (allAlbums[i].albumId.toString() == audioContent.albumId.toString()) {
                            allAlbums[i].addAudioContent(audioContent)
                            allAlbums[i].addNumberOfSongs()
                        }
                    }
                }

            } while (cursor!!.moveToNext())
            cursor!!.close()
        }

        audioContext.externalCacheDir
        return allAlbums
    }

    /**
     * Returns an ArrayList of Strings which represent names of all artist with music in the [MediaStore]
     * database of the device
     */
    fun getAllArtistIds(contentLocation: Uri?): ArrayList<String> {
        val allArtistIds = ArrayList<String>()

        cursor = audioContext.contentResolver.query(contentLocation!!, audioProjection, selection, null, "LOWER (" + MediaStore.Audio.Artists.ARTIST + ") ASC")

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val artistId = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                if (!allArtistIds.contains(artistId.toString())) {
                    allArtistIds.add(artistId.toString())
                }
            } while (cursor!!.moveToNext())
        }
        cursor!!.close()
        return allArtistIds
    }

    /**
     * Returns and ArrayList of Artists as [AudioArtistContent]
     * objects from the android [MediaStore] database
     */
    fun getAllArtists(ids: ArrayList<String>, contentLocation: Uri): ArrayList<AudioArtistContent> {
        val audioArtistContents = ArrayList<AudioArtistContent>()
        for (i in ids.indices) {
            val artistAlbums = getAllAlbumsByArtistId(ids[i], contentLocation)
            audioArtistContents.add(AudioArtistContent(ids[i], artistAlbums))
        }
        return audioArtistContents
    }

    fun getMusicMetaData(path: String): AudioContent {
        val audioContent = AudioContent()

        cursor = audioContext.contentResolver.query(
            externalContentUri,
            audioProjection,
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf("%$path%"),
            null
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
            audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
            audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            audioContent.fileStringUri = Uri.withAppendedPath(externalContentUri, audioContent.musicID.toString()).toString()
            audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
            audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
            audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
            audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
            audioContent.dateTaken = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_TAKEN))
            audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), audioContent.albumId.toString()).toString()
            audioContent.track = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.TRACK))
            audioContent.mimeType = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
            audioContent.year = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR))
        }
        cursor!!.close()

        return audioContent
    }

    /**
     * returns an ArrayList of audioContent whose names all match the search string
     */
    fun searchMusic(audioTitle: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()

        cursor = audioContext.contentResolver.query(
            externalContentUri,
            audioProjection,
            MediaStore.Audio.Media.TITLE + " like ? ",
            arrayOf("%$audioTitle%"),
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val audioContent = AudioContent()

                audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), audioContent.albumId.toString()).toString()
                audioContent.fileStringUri = Uri.withAppendedPath(externalContentUri, audioContent.musicID.toString()).toString()
                audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                audioContents.add(audioContent)
            } while (cursor!!.moveToNext())
        }

        cursor!!.close()
        return audioContents
    }

    /**
     * returns an ArrayList of albumHolder whose names all match the search string
     */
    fun searchAlbum(albumName: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()

        cursor = audioContext.contentResolver.query(
            externalContentUri,
            audioProjection,
            MediaStore.Audio.Media.ALBUM + " like ? ",
            arrayOf("%$albumName%"),
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val audioContent = AudioContent()

                audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), audioContent.albumId.toString()).toString()
                audioContent.fileStringUri = Uri.withAppendedPath(externalContentUri, audioContent.musicID.toString()).toString()
                audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                audioContents.add(audioContent)
            } while (cursor!!.moveToNext())
        }
        cursor!!.close()
        return audioContents
    }

    /**
     * returns an ArrayList of artistHolder whose names all match the search string
     */
    fun searchArtist(artistName: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()

        cursor = audioContext.contentResolver.query(
            externalContentUri,
            audioProjection,
            MediaStore.Audio.Media.ARTIST + " like ? ",
            arrayOf("%$artistName%"),
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val audioContent = AudioContent()

                audioContent.albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                audioContent.name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioContent.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                audioContent.musicID = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                audioContent.duration = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                audioContent.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                audioContent.artUri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), audioContent.albumId.toString()).toString()
                audioContent.fileStringUri = Uri.withAppendedPath(externalContentUri, audioContent.musicID.toString()).toString()
                audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                audioContent.filePath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioContent.musicSize = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioContent.artist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                audioContents.add(audioContent)
            } while (cursor!!.moveToNext())
        }

        cursor!!.close()
        return audioContents
    }

    companion object {
        val audioProjection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATE_TAKEN,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
        )

        val genresProjection = arrayOf(
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
        )

        private var audioGet: AudioGet? = null
        const val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val externalContentUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val internalContentUri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        private var cursor: Cursor? = null
        private var genresCursor: Cursor? = null
        fun getInstance(context: Context): AudioGet? {
            if (audioGet == null) {
                audioGet = AudioGet(context)
            }
            return audioGet
        }
    }
}