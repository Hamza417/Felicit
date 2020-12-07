package app.simple.felicit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.simple.felicit.model.Song

@Database(entities = [Song::class], exportSchema = false, version = 1)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao?

    companion object {
        private var instance: SongDatabase? = null
        @Synchronized
        fun getInstance(context: Context?, DB_NAME: String?): SongDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context!!, SongDatabase::class.java, DB_NAME!!)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}