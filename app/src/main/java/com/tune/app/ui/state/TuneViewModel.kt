package com.tune.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tune.app.data.model.Song
import com.tune.app.data.repo.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TuneViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val customPlaylists = MutableStateFlow(listOf("Roadtrip"))
    private val favoriteSongIds = MutableStateFlow(setOf<Long>())
    private val nowPlayingSong = MutableStateFlow<Song?>(null)

    val songs: StateFlow<List<Song>> = repository.observeSongs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val filteredSongs: StateFlow<List<Song>> = combine(songs, query) { allSongs, q ->
        if (q.isBlank()) allSongs
        else allSongs.filter {
            it.title.contains(q, true) || it.artist.contains(q, true) || it.album.contains(q, true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<String>> = combine(customPlaylists, favoriteSongIds) { custom, favorites ->
        listOf("Favorites (${favorites.size})") + custom
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Favorites (0)"))

    val favorites: StateFlow<Set<Long>> = favoriteSongIds
    val searchQuery: StateFlow<String> = query
    val currentSong: StateFlow<Song?> = nowPlayingSong

    fun refreshLibrary() = viewModelScope.launch { repository.refreshLibrary() }

    fun setSearchQuery(value: String) {
        query.value = value
    }

    fun selectSong(song: Song) {
        nowPlayingSong.value = song
    }

    fun toggleFavorite(songId: Long) {
        favoriteSongIds.update { ids -> if (songId in ids) ids - songId else ids + songId }
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        customPlaylists.update { old -> if (old.contains(name)) old else old + name }
    }
}
