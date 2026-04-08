package com.tune.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tune.app.data.model.Song

@Database(entities = [Song::class], version = 3, exportSchema = false)
abstract class TuneDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
