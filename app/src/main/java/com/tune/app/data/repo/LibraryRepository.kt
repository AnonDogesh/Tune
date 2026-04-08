package com.tune.app.data.repo

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.tune.app.data.db.SongDao
import com.tune.app.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val songDao: SongDao,
    @ApplicationContext private val context: Context
) {
    fun observeSongs(): Flow<List<Song>> = songDao.observeSongs()

    suspend fun refreshLibrary() = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.IS_MUSIC
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val orderBy = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(collection, projection, selection, null, orderBy)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val relativePathCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol) ?: "Unknown"
                val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                val album = cursor.getString(albumCol) ?: "Unknown Album"
                val durationMs = cursor.getLong(durationCol)
                val albumId = cursor.getLong(albumIdCol)
                val folderPath = cursor.getString(relativePathCol)?.trim('/').orEmpty()
                val sizeBytes = cursor.getLong(sizeCol)
                val songUri = ContentUris.withAppendedId(collection, id).toString()
                val albumArtUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId).toString()

                songs += Song(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = formatDuration(durationMs),
                    path = songUri,
                    albumArtUri = albumArtUri,
                    folderPath = folderPath,
                    sizeBytes = sizeBytes
                )
            }
        }

        songDao.upsertSongs(songs)
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSec = durationMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%d:%02d".format(min, sec)
    }
}
