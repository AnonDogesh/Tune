package com.tune.app.di

import android.content.Context
import androidx.room.Room
import com.tune.app.data.db.SongDao
import com.tune.app.data.db.TuneDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TuneDatabase =
        Room.databaseBuilder(context, TuneDatabase::class.java, "tune.db").build()

    @Provides
    fun provideSongDao(db: TuneDatabase): SongDao = db.songDao()
}
