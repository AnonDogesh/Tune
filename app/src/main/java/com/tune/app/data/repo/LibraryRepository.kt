package com.tune.app.data.repo

import com.tune.app.data.db.SongDao
import com.tune.app.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val songDao: SongDao
) {
    fun observeSongs(): Flow<List<Song>> = songDao.observeSongs()

    suspend fun refreshLibrary() {
        val sample = List(20) {
            Song(
                id = it.toLong(),
                title = "Offline Song $it",
                artist = "Artist ${it % 5}",
                album = "Album ${it % 4}",
                duration = "3:${10 + it}"
            )
        }
        songDao.upsertSongs(sample)
    }
}
