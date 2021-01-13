package app.simple.felicit.database

import androidx.room.*
import app.simple.felicit.models.AudioContent

@Dao
interface SongDao {

    @Query ("SELECT * FROM songs")
    fun getSongListUnaltered() : MutableList<AudioContent>

    @Query("SELECT * FROM songs ORDER BY title COLLATE nocase ASC")
    fun getSongLinearList(): MutableList<AudioContent>

    @Query ("SELECT DISTINCT * FROM songs ORDER BY RANDOM() LIMIT 20")
    fun getSongRandomList(): MutableList<AudioContent>

    @Query("SELECT DISTINCT * FROM songs GROUP BY artists ORDER BY artists COLLATE nocase ASC")
    fun getArtistList(): MutableList<AudioContent>

    @Query("SELECT DISTINCT * FROM songs GROUP BY album ORDER BY album COLLATE nocase ASC")
    fun getAlbumList(): List<AudioContent>

    @Query("SELECT * FROM songs ORDER BY date_added DESC limit 50")
    fun getDateList(): List<AudioContent>

    @Query("SELECT * FROM songs ORDER BY date_last_played DESC")
    fun getHistoryList(): List<AudioContent>

    /**
     * Insert and save song list to Database
     *
     * @param song loads song details
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: MutableList<AudioContent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: AudioContent)

    /**
     * Update song
     *
     * @param song that will be update
     */
    @Update
    suspend fun updateSong(song: AudioContent)

    /**
     * @param song removes a song from the list
     */
    @Delete
    suspend fun deleteSong(song: AudioContent)

    /**
     * Deletes the entire database, possibly to create a new one
     */
    @Query("DELETE FROM songs")
    suspend fun nukeTable()
}