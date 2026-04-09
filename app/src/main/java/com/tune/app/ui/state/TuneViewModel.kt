package com.tune.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tune.app.data.model.Song
import com.tune.app.data.repo.LibraryRepository
import com.tune.app.data.repo.UserStateRepository
import com.tune.app.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class TuneViewModel @Inject constructor(
    private val repository: LibraryRepository,
    private val userState: UserStateRepository,
    private val playback: PlaybackController
) : ViewModel() {
    data class EqBand(val label: String, val level: Short)

    enum class RepeatMode { Off, All, One }

    private val query = MutableStateFlow("")
    private val searchHistory = MutableStateFlow(listOf("Chill lofi", "Arctic Monkeys", "After Hours"))
    private val customPlaylists = MutableStateFlow(userState.loadCustomPlaylists())
    private val playlistSongs = MutableStateFlow(userState.loadPlaylistSongs())
    private val favoriteSongIds = MutableStateFlow(userState.loadFavorites())
    private val hiddenSongIds = MutableStateFlow(userState.loadHiddenSongs())
    private val excludedFolders = MutableStateFlow(userState.loadExcludedFolders())

    private val nowPlayingSong = MutableStateFlow<Song?>(null)
    private val activeQueue = MutableStateFlow<List<Song>>(emptyList())
    private val activeQueueName = MutableStateFlow("Library")
    private val shuffleEnabled = MutableStateFlow(false)
    private val repeatMode = MutableStateFlow(RepeatMode.Off)
    private val recentPlayedIds = MutableStateFlow<List<Long>>(emptyList())
    private val eqEnabled = MutableStateFlow(false)
    private val eqBands = MutableStateFlow<List<EqBand>>(emptyList())
    private val eqPresetNames = MutableStateFlow<List<String>>(emptyList())
    private val eqSelectedPreset = MutableStateFlow(-1)
    private val eqLevelRange = MutableStateFlow((-1500).toShort() to 1500.toShort())

    private val rawSongs: StateFlow<List<Song>> = repository.observeSongs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val songs: StateFlow<List<Song>> = combine(rawSongs, hiddenSongIds, excludedFolders) { all, hidden, excluded ->
        all.filterNot { song ->
            song.id in hidden || (song.folderPath.isNotBlank() && song.folderPath in excluded)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val libraryFolders: StateFlow<List<String>> = rawSongs
        .combine(excludedFolders) { all, _ ->
            all.map { it.folderPath.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val excludedLibraryFolders: StateFlow<Set<String>> = excludedFolders

    val visibleAudioSizeBytes: StateFlow<Long> = songs
        .combine(excludedFolders) { visibleSongs, _ -> visibleSongs.sumOf { it.sizeBytes } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val filteredSongs: StateFlow<List<Song>> = combine(songs, query) { allSongs, q ->
        if (q.isBlank()) allSongs else allSongs.filter {
            it.title.contains(q, true) || it.artist.contains(q, true) || it.album.contains(q, true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSongs: StateFlow<List<Song>> = combine(songs, recentPlayedIds) { allSongs, ids ->
        ids.mapNotNull { id -> allSongs.find { it.id == id } }.take(10)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<String>> = combine(customPlaylists, favoriteSongIds) { custom, favorites ->
        listOf("Favorites (${favorites.size})") + custom
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Favorites (0)"))

    val favorites: StateFlow<Set<Long>> = favoriteSongIds
    val searchQuery: StateFlow<String> = query
    val recentSearches: StateFlow<List<String>> = searchHistory
    val currentSong: StateFlow<Song?> = nowPlayingSong
    val isPlaying: StateFlow<Boolean> = playback.isPlaying
    val positionMs: StateFlow<Long> = playback.positionMs
    val durationMs: StateFlow<Long> = playback.durationMs
    val queueName: StateFlow<String> = activeQueueName
    val queueSongs: StateFlow<List<Song>> = activeQueue
    val isShuffleEnabled: StateFlow<Boolean> = shuffleEnabled
    val currentRepeatMode: StateFlow<RepeatMode> = repeatMode
    val customPlaylistNames: StateFlow<List<String>> = customPlaylists
    val equalizerEnabled: StateFlow<Boolean> = eqEnabled
    val equalizerBands: StateFlow<List<EqBand>> = eqBands
    val equalizerPresets: StateFlow<List<String>> = eqPresetNames
    val equalizerSelectedPreset: StateFlow<Int> = eqSelectedPreset
    val equalizerLevelRange: StateFlow<Pair<Short, Short>> = eqLevelRange

    init {
        viewModelScope.launch {
            while (true) {
                playback.tick()
                delay(500)
            }
        }
        viewModelScope.launch {
            playback.trackEnded.collect { handleTrackEnded() }
        }
        viewModelScope.launch {
            playback.audioSessionId.collect {
                refreshEqualizerState()
            }
        }
    }

    fun refreshLibrary() = viewModelScope.launch {
        repository.refreshLibrary()
        val ids = songs.value.map { it.id }.toSet()
        playlistSongs.update { map -> map.mapValues { (_, values) -> values.intersect(ids) } }
        userState.savePlaylistSongs(playlistSongs.value)
        favoriteSongIds.update { it.intersect(ids) }
        userState.saveFavorites(favoriteSongIds.value)
    }

    fun setSearchQuery(value: String) {
        query.value = value
        if (value.length > 2) searchHistory.update { old -> (listOf(value) + old.filterNot { it.equals(value, true) }).take(5) }
    }

    fun playSongFromLibrary(song: Song) {
        activeQueue.value = songs.value
        activeQueueName.value = "Library"
        playSong(song)
    }

    fun playSongFromPlaylist(playlistLabel: String, song: Song) {
        activeQueue.value = getPlaylistSongs(playlistLabel)
        activeQueueName.value = playlistLabel
        playSong(song)
    }

    fun playFromQueue(song: Song) {
        if (song in activeQueue.value) playSong(song)
    }

    private fun playSong(song: Song) {
        nowPlayingSong.value = song
        recentPlayedIds.update { ids -> (listOf(song.id) + ids.filterNot { it == song.id }).take(10) }
        if (song.path.isNotBlank()) playback.playFromUri(song.path)
    }

    private fun handleTrackEnded() {
        when (repeatMode.value) {
            RepeatMode.One -> {
                playback.seekTo(0)
                playback.play()
            }
            else -> nextSong()
        }
    }

    fun togglePlayPause() = playback.togglePlayPause()

    fun nextSong() {
        val queue = activeQueue.value.ifEmpty { songs.value }
        val current = nowPlayingSong.value ?: return
        if (queue.isEmpty()) return

        val next = if (shuffleEnabled.value) queue[Random.nextInt(queue.size)] else {
            val index = queue.indexOfFirst { it.id == current.id }
            when {
                index == -1 -> queue.first()
                index == queue.lastIndex && repeatMode.value == RepeatMode.All -> queue.first()
                index == queue.lastIndex -> current
                else -> queue[index + 1]
            }
        }

        if (next.id != current.id || repeatMode.value == RepeatMode.All || shuffleEnabled.value) playSong(next)
    }

    fun previousSong() {
        val queue = activeQueue.value.ifEmpty { songs.value }
        val current = nowPlayingSong.value ?: return
        if (queue.isEmpty()) return

        val prev = if (shuffleEnabled.value) queue[Random.nextInt(queue.size)] else {
            val index = queue.indexOfFirst { it.id == current.id }
            if (index <= 0) queue.last() else queue[index - 1]
        }
        playSong(prev)
    }

    fun seekToFraction(progress: Float) = playback.seekTo((durationMs.value * progress).toLong())

    fun toggleFavorite(songId: Long) {
        favoriteSongIds.update { ids -> if (songId in ids) ids - songId else ids + songId }
        userState.saveFavorites(favoriteSongIds.value)
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        customPlaylists.update { old -> if (old.contains(name)) old else old + name }
        playlistSongs.update { map -> if (map.containsKey(name)) map else map + (name to emptySet()) }
        userState.saveCustomPlaylists(customPlaylists.value)
        userState.savePlaylistSongs(playlistSongs.value)
    }

    fun addCurrentSongToPlaylist(name: String) {
        val songId = nowPlayingSong.value?.id ?: return
        addSongToPlaylist(name, songId)
    }

    fun addSongToPlaylist(name: String, songId: Long) {
        if (name.startsWith("Favorites")) return
        playlistSongs.update { map -> map + (name to (map[name].orEmpty() + songId)) }
        userState.savePlaylistSongs(playlistSongs.value)
    }

    fun removeSongFromPlaylist(name: String, songId: Long) {
        if (name.startsWith("Favorites")) {
            toggleFavorite(songId)
            return
        }
        playlistSongs.update { map -> map + (name to (map[name].orEmpty() - songId)) }
        userState.savePlaylistSongs(playlistSongs.value)
    }

    fun getPlaylistSongs(playlistLabel: String): List<Song> {
        val all = songs.value
        return if (playlistLabel.startsWith("Favorites")) all.filter { it.id in favoriteSongIds.value }
        else all.filter { it.id in playlistSongs.value[playlistLabel].orEmpty() }
    }

    fun removeSongFromLibrary(songId: Long) {
        hiddenSongIds.update { it + songId }
        userState.saveHiddenSongs(hiddenSongIds.value)
        activeQueue.update { it.filterNot { song -> song.id == songId } }
        if (nowPlayingSong.value?.id == songId) nextSong()
    }

    fun setExcludedFolders(folders: Set<String>) {
        excludedFolders.value = folders
        userState.saveExcludedFolders(folders)
    }

    fun toggleShuffle() { shuffleEnabled.update { !it } }

    fun cycleRepeatMode() {
        repeatMode.value = when (repeatMode.value) {
            RepeatMode.Off -> RepeatMode.All
            RepeatMode.All -> RepeatMode.One
            RepeatMode.One -> RepeatMode.Off
        }
    }

    fun refreshEqualizerState() {
        if (!playback.isEqualizerAvailable()) {
            eqEnabled.value = false
            eqBands.value = emptyList()
            eqPresetNames.value = emptyList()
            eqSelectedPreset.value = -1
            return
        }

        eqEnabled.value = playback.isEqualizerEnabled()
        eqLevelRange.value = playback.equalizerBandLevelRange()
        val freqs = playback.equalizerBandFrequenciesHz()
        val levels = playback.equalizerBandLevels()
        eqBands.value = freqs.zip(levels).map { (freq, level) ->
            EqBand(label = formatEqFreq(freq), level = level)
        }
        eqPresetNames.value = playback.equalizerPresetNames()
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        playback.setEqualizerEnabled(enabled)
        eqEnabled.value = enabled
    }

    fun setEqualizerBandLevel(index: Int, normalized: Float) {
        val (min, max) = eqLevelRange.value
        val level = (min + ((max - min) * normalized)).toInt().toShort()
        playback.setEqualizerBandLevel(index, level)
        refreshEqualizerState()
        eqSelectedPreset.value = -1
    }

    fun applyEqualizerPreset(index: Int) {
        playback.applyEqualizerPreset(index)
        eqSelectedPreset.value = index
        refreshEqualizerState()
    }

    private fun formatEqFreq(freqHz: Int): String {
        return if (freqHz >= 1000) {
            String.format("%.1fkHz", freqHz / 1000f)
        } else {
            "${freqHz}Hz"
        }
    }
}
