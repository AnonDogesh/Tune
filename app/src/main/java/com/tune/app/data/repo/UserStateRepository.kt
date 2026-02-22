package com.tune.app.data.repo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStateRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("tune_user_state", Context.MODE_PRIVATE)

    fun loadCustomPlaylists(): List<String> =
        prefs.getString(KEY_CUSTOM_PLAYLISTS, "Roadtrip")
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: listOf("Roadtrip")

    fun saveCustomPlaylists(values: List<String>) {
        prefs.edit().putString(KEY_CUSTOM_PLAYLISTS, values.joinToString("|")).apply()
    }

    fun loadPlaylistSongs(): Map<String, Set<Long>> {
        val raw = prefs.getString(KEY_PLAYLIST_SONGS, "") ?: ""
        if (raw.isBlank()) return mapOf("Roadtrip" to emptySet())
        return raw.split(";;")
            .mapNotNull { part ->
                val pieces = part.split("::")
                if (pieces.size != 2) null
                else {
                    val name = pieces[0]
                    val ids = pieces[1].split(',').mapNotNull { it.toLongOrNull() }.toSet()
                    name to ids
                }
            }
            .toMap()
    }

    fun savePlaylistSongs(map: Map<String, Set<Long>>) {
        val encoded = map.entries.joinToString(";;") { (k, v) -> "$k::${v.joinToString(",")}" }
        prefs.edit().putString(KEY_PLAYLIST_SONGS, encoded).apply()
    }

    fun loadFavorites(): Set<Long> =
        prefs.getString(KEY_FAVORITES, "")
            ?.split(',')
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()

    fun saveFavorites(values: Set<Long>) {
        prefs.edit().putString(KEY_FAVORITES, values.joinToString(",")).apply()
    }

    fun loadHiddenSongs(): Set<Long> =
        prefs.getString(KEY_HIDDEN, "")
            ?.split(',')
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()

    fun saveHiddenSongs(values: Set<Long>) {
        prefs.edit().putString(KEY_HIDDEN, values.joinToString(",")).apply()
    }

    companion object {
        private const val KEY_CUSTOM_PLAYLISTS = "custom_playlists"
        private const val KEY_PLAYLIST_SONGS = "playlist_songs"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_HIDDEN = "hidden_songs"
    }
}
