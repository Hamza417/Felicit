package app.simple.felicit.helper

import android.content.Context
import androidx.room.Room
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.models.AudioContent
import kotlinx.coroutines.*

object SongListDatabaseHelper {

    const val lastList = "last_list.db"
    private const val history = "history.db"

    fun saveHistoryIntoDatabase(source: String, list: MutableList<AudioContent>, context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            val room = Room.databaseBuilder(context, SongDatabase::class.java, source).build()
            room.songDao()?.nukeTable()
            room.songDao()?.insertSong(list)
            room.close()
        }
    }

    suspend fun getSongListFromDatabase(source: String, context: Context): ArrayList<AudioContent> {
        var value: MutableList<AudioContent>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val room = Room.databaseBuilder(context, SongDatabase::class.java, source).build()
            value = room.songDao()?.getSongLinearList()!!
            room.close()
            return@async value
        }

        waitFor.await()

        return value as ArrayList<AudioContent>
    }

    fun saveHistoryIntoDatabase(song: AudioContent, context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            song.dateLastPlayed = System.currentTimeMillis()
            val room = Room.databaseBuilder(context, SongDatabase::class.java, history).build()
            room.songDao()?.insertSong(song)
            room.close()
        }
    }
}