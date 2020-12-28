package app.simple.felicit.loader

import android.content.Context
import androidx.room.Room
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.medialoader.mediamodels.AudioContent
import kotlinx.coroutines.*

fun scanSongList(songs: MutableList<AudioContent>, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val songDatabase = Room.databaseBuilder(context, SongDatabase::class.java, "last_list.db").build()
        songDatabase.songDao()?.nukeTable()
        songDatabase.songDao()?.insertSong(songs)
        songDatabase.close()
    }
}

suspend fun getLastList(context: Context): MutableList<AudioContent> {
    var value: MutableList<AudioContent> = arrayListOf()

    val waitFor = CoroutineScope(Dispatchers.IO).async {
        val songDatabase = Room.databaseBuilder(context, SongDatabase::class.java, "last_list.db").build()
        value = songDatabase.songDao()?.getSongLinearList() as MutableList<AudioContent>
        songDatabase.close()
        return@async value
    }

    waitFor.await()
    return value
}