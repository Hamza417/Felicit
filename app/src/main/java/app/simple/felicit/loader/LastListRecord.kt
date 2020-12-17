package app.simple.felicit.loader

import android.content.Context
import androidx.room.Room
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.medialoader.mediaHolders.AudioContent
import kotlinx.coroutines.*

fun scanSongList(songs: MutableList<AudioContent>, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val songDatabase = Room.databaseBuilder(context, SongDatabase::class.java, "last_list.db").build()

        songDatabase.songDao()?.nukeTable()

        for (i in songs.indices) {
            songDatabase.songDao()?.insertSong(songs)
        }
    }
}

suspend fun getLastList(context: Context): MutableList<AudioContent> {
    var value: MutableList<AudioContent> = arrayListOf()

    val waitFor = CoroutineScope(Dispatchers.IO).async {
        val songDatabase = Room.databaseBuilder(context, SongDatabase::class.java, "last_list.db").build()
        value = songDatabase.songDao()?.getList() as MutableList<AudioContent>
        return@async value
    }

    waitFor.await()
    return value
}