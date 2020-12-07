package app.simple.felicit.database

import androidx.room.*
import app.simple.felicit.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY id ASC")
    fun getList(): List<Song>

    @Query("SELECT * FROM songs ORDER BY title COLLATE nocase ASC")
    fun getSongLinearList(): List<Song>

    @Query("SELECT * FROM songs ORDER BY title COLLATE nocase DESC")
    fun getSongReverseList(): List<Song>

    @Query("SELECT DISTINCT * FROM songs GROUP BY artists ORDER BY artists COLLATE nocase ASC")
    fun getArtistList(): List<Song>

    @Query("SELECT DISTINCT * FROM songs GROUP BY album ORDER BY album COLLATE nocase ASC")
    fun getAlbumList(): List<Song>

    @Query("SELECT * FROM songs ORDER BY date DESC limit 50")
    fun getDateList(): List<Song>

    @Query("SELECT DISTINCT * FROM songs GROUP BY genre ORDER BY genre COLLATE nocase ASC")
    fun getGenreList(): List<Song>

    @Query("SELECT DISTINCT * FROM songs GROUP BY folder ORDER BY folder COLLATE nocase ASC")
    fun getFoldersList(): List<Song>

    /**
     * Insert and save song to Database
     *
     * @param song loads song details
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    /**
     * Update note
     *
     * @param song that will be update
     */
    @Update
    suspend fun updateSong(song: Song)

    /**
     * @param song removes a song from the list
     */
    @Delete
    suspend fun deleteSong(song: Song)
}