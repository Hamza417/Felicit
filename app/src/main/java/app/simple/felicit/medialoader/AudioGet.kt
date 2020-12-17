package app.simple.felicit.medialoader

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import app.simple.felicit.medialoader.mediaHolders.AudioContent
import app.simple.felicit.medialoader.mediaHolders.AudioAlbumContent
import app.simple.felicit.medialoader.mediaHolders.AudioArtistContent
import app.simple.felicit.medialoader.mediaHolders.audioFolderContent
import java.io.File
import java.util.*

@SuppressLint("NewApi")
@Suppress("deprecation")
class AudioGet private constructor(context: Context) {
    private val audioContext: Context = context.applicationContext

    /**
     * Returns an Arraylist of [AudioContent]
     */
    fun getAllAudioContent(contentLocation: Uri?): ArrayList<AudioContent> {
        val allAudioContent = ArrayList<AudioContent>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(contentLocation!!, projection, selection, null, "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC")
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val audioContent = AudioContent()
                    val songName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = songName
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    val contentUri = Uri.withAppendedPath(contentLocation, id.toString())
                    audioContent.fileStringUri = contentUri.toString()

                    //for android 10 exclusively
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Uri contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                        try {
                            AssetFileDescriptor file = audioContext.getContentResolver().openAssetFileDescriptor(contentUri, "r");
                            audioContent.setMusicPathQ(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }*/try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ignored: Exception) {
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ignored: Exception) {
                        audioContent.dateModified = 0
                    }
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val albumName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val artistName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName
                    allAudioContent.add(audioContent)
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }

        //try saving in cache for better loading next time
        audioContext.externalCacheDir
        return allAudioContent
    }

    /**
     * Returns an ArrayList of [AudioAlbumContent]
     */
    fun getAllAlbums(contentLocation: Uri?): ArrayList<AudioAlbumContent> {
        val audioAlbumContents = ArrayList<AudioAlbumContent>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(contentLocation!!, projection, selection, null, "LOWER (" + MediaStore.Audio.Media.ALBUM + ") ASC")
        val albumNames = ArrayList<String>()
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    var album: AudioAlbumContent
                    val audioContent = AudioContent()
                    val song_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = song_name
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val album_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = album_name
                    val album_id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, album_id.toString())
                    audioContent.artUri = imageUri.toString()
                    val contentUri = Uri.withAppendedPath(contentLocation, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val artist_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artist_name
                    if (!albumNames.contains(album_id.toString())) {
                        albumNames.add(album_id.toString())
                        album = AudioAlbumContent(album_name, album_id, imageUri, artist_name)
                        album.addAudioContent(audioContent)
                        album.addNumberOfSongs()
                        audioAlbumContents.add(album)
                    } else {
                        for (i in audioAlbumContents.indices) {
                            if (audioAlbumContents[i].albumId.toString() == album_id.toString()) {
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
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(contentLocation, projection,
                                                    MediaStore.Audio.Artists.ARTIST + " like ? ", arrayOf("%$artist_id%"), "LOWER (" + MediaStore.Audio.Artists.ARTIST + ") ASC")
        val albumNames = ArrayList<String>()
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    var album: AudioAlbumContent
                    val audioContent = AudioContent()
                    val song_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = song_name
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName
                    val contentUri = Uri.withAppendedPath(contentLocation, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val artistName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName
                    if (!albumNames.contains(albumId.toString())) {
                        albumNames.add(albumId.toString())
                        album = AudioAlbumContent(albumName, albumId, imageUri, artistName)
                        album.addAudioContent(audioContent)
                        album.addNumberOfSongs()
                        allAlbums.add(album)
                    } else {
                        for (i in allAlbums.indices) {
                            if (allAlbums[i].albumId.toString() == albumId.toString()) {
                                allAlbums[i].addAudioContent(audioContent)
                                allAlbums[i].addNumberOfSongs()
                            }
                        }
                    }
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }

        //try saving in cache for better loading next time
        audioContext.externalCacheDir
        return allAlbums
    }

    /**
     * Returns an ArrayList of Strings which represent names of all artist with music in the [MediaStore]
     * database of the device
     */
    fun getAllArtistIds(contentLocation: Uri?): ArrayList<String> {
        val allArtistIds = ArrayList<String>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST_KEY, MediaStore.Audio.Artists.ARTIST)
        cursor = audioContext.contentResolver.query(contentLocation!!, projection, selection, null, "LOWER (" + MediaStore.Audio.Artists.ARTIST + ") ASC")
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val artistId = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                    if (!allArtistIds.contains(artistId.toString())) {
                        allArtistIds.add(artistId.toString())
                    }
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        return allArtistIds
    }

    /**
     * Returns and ArrayList of Artists as [AudioArtistContent]
     * objects from the android [MediaStore] database
     */
    fun getAllArtists(ids: ArrayList<String>, contentLocation: Uri): ArrayList<AudioArtistContent> {
        val audioArtistContents = ArrayList<AudioArtistContent>()
        for (i in ids.indices) {
            val artist = AudioArtistContent()
            val artistAlbums = getAllAlbumsByArtistId(ids[i], contentLocation)
            artist.albums = artistAlbums
            artist.artistName = ids[i]
            audioArtistContents.add(artist)
        }
        return audioArtistContents
    }//try saving in cache for better loading next time

    /**
     * Returns and ArrayList of [audioFolderContent] from the android MediaStore
     */
    val allAudioFolderContent: ArrayList<audioFolderContent>
        get() {
            val musicFolders = ArrayList<audioFolderContent>()
            val allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.BUCKET_ID, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
                                     MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID)
            cursor = audioContext.contentResolver.query(allSongsUri, projection, selection, null, "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC")
            val folders = ArrayList<Int>()
            try {
                if (cursor != null) {
                    cursor!!.moveToFirst()
                }
                do {
                    val audioFolder = audioFolderContent()
                    val audioContent = AudioContent()
                    val dataPath = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val path = File(dataPath)
                    val parent = File(path.parent!!)
                    val parentName = parent.name
                    val parentPath = parent.absolutePath
                    val duration = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.musicSize = duration.toLong()
                    val bucketId = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_ID))
                    val songName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = songName
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    audioContent.filePath = dataPath
                    val audio = File(dataPath)
                    audioContent.musicSize = audio.length()
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val album_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = album_name
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val album_id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, album_id.toString())
                    audioContent.artUri = imageUri.toString()
                    val artist_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artist_name
                    if (!folders.contains(bucketId)) {
                        folders.add(bucketId)
                        audioFolder.bucket_id = bucketId
                        audioFolder.folderName = parentName
                        audioFolder.folderPath = parentPath
                        audioFolder.musicFiles.add(audioContent)
                        musicFolders.add(audioFolder)
                    } else {
                        for (i in musicFolders.indices) {
                            if (musicFolders[i].folderName == parentName) {
                                musicFolders[i].musicFiles.add(audioContent)
                            }
                        }
                    }
                } while (cursor!!.moveToNext())
                cursor!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in musicFolders.indices) {
                Log.d("audio folders", musicFolders[i].folderName + " and path = " + musicFolders[i].folderPath + " " + musicFolders[i].numberOfSongs)
            }

            //try saving in cache for better loading next time
            audioContext.externalCacheDir
            return musicFolders
        }

    fun getMusicMetaData(dataPath: String): AudioContent {
        val audioContent = AudioContent()
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(externalContentUri, projection,
                                                    MediaStore.Audio.Media.DATA + " like ? ", arrayOf("%$dataPath%"), null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val song_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = song_name
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val albumName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val artistName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        return audioContent
    }

    /**
     * returns an ArrayList of audioContent whose names all match the search string
     */
    fun searchMusic(audioTitle: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(externalContentUri, projection,
                                                    MediaStore.Audio.Media.TITLE + " like ? ", arrayOf("%$audioTitle%"), "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC")
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val audioContent = AudioContent()
                    val songName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = songName
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val albumName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val artistName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName
                    audioContents.add(audioContent)
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        return audioContents
    }

    /**
     * returns an ArrayList of albumHolder whose names all match the search string
     */
    fun searchAlbum(albumName: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.TITLE,
                                                             MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(externalContentUri, projection,
                                                    MediaStore.Audio.Media.ALBUM + " like ? ", arrayOf("%$albumName%"), "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC")
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val audioContent = AudioContent()
                    val song_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = song_name
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val albumName1 = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName1
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val artistName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName
                    audioContents.add(audioContent)
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        return audioContents
    }

    /**
     * returns an ArrayList of artistHolder whose names all match the search string
     */
    fun searchArtist(artistName: String): ArrayList<AudioContent> {
        val audioContents = ArrayList<AudioContent>()
        @SuppressLint("InlinedApi") val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
                                                             MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST_ID)
        cursor = audioContext.contentResolver.query(externalContentUri, projection,
                                                    MediaStore.Audio.Media.ARTIST + " like ? ", arrayOf("%$artistName%"), "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC")
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val audioContent = AudioContent()
                    val song_name = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    audioContent.name = song_name
                    val songTitle = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioContent.title = songTitle
                    val id = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media._ID))
                    audioContent.musicID = id
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString())
                    audioContent.fileStringUri = contentUri.toString()
                    try {
                        audioContent.dateAdded = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateAdded = 0
                    }
                    try {
                        audioContent.dateModified = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        audioContent.dateModified = 0
                    }
                    val path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioContent.filePath = path
                    val audio = File(path)
                    audioContent.musicSize = audio.length()
                    val albumName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    audioContent.album = albumName
                    @SuppressLint("InlinedApi") val dur = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    audioContent.duration = dur
                    val albumId = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())
                    audioContent.artUri = imageUri.toString()
                    val artistName1 = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioContent.artist = artistName1
                    audioContents.add(audioContent)
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        return audioContents
    }

    companion object {
        private var audioGet: AudioGet? = null
        val externalContentUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val internalContentUri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        private var cursor: Cursor? = null
        fun getInstance(context: Context): AudioGet? {
            if (audioGet == null) {
                audioGet = AudioGet(context)
            }
            return audioGet
        }
    }
}