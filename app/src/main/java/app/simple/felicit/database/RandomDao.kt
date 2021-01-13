package app.simple.felicit.database

import androidx.room.*
import app.simple.felicit.models.AudioContent

@Dao
interface RandomDao {
    @Query("SELECT * FROM songs")
    fun getList(): MutableList<AudioContent>

    /**
     * Insert and save song to Database
     *
     * @param song loads song details
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: List<AudioContent>)

    /**
     * Update note
     *
     * @param song that will be updated
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