package app.simple.felicit.database

import androidx.room.*
import app.simple.felicit.medialoader.mediaHolders.AudioContent

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getList(): MutableList<AudioContent>

    @Query("SELECT * FROM songs ORDER BY title COLLATE nocase ASC")
    fun getSongLinearList(): MutableList<AudioContent>

    @Query("SELECT * FROM songs ORDER BY artists COLLATE nocase ASC")
    fun getArtistsLinearList(): MutableList<AudioContent>

    /**
     * Insert and save song to Database
     *
     * @param song loads song details
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: MutableList<AudioContent>)

    /**
     * Update note
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
    fun nukeTable()
}