package com.tune.app.playback

import android.content.Context
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }
    private var equalizer: Equalizer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs

    private val _durationMs = MutableStateFlow(1L)
    val durationMs: StateFlow<Long> = _durationMs

    private val _trackEnded = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val trackEnded: SharedFlow<Unit> = _trackEnded

    private val _audioSessionId = MutableStateFlow(0)
    val audioSessionId: StateFlow<Int> = _audioSessionId

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _trackEnded.tryEmit(Unit)
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                _positionMs.value = player.currentPosition
                _durationMs.value = if (player.duration > 0) player.duration else 1L
                val sessionId = player.audioSessionId
                if (sessionId > 0 && _audioSessionId.value != sessionId) {
                    _audioSessionId.value = sessionId
                    initEqualizer(sessionId)
                }
            }
        })
    }

    private fun initEqualizer(sessionId: Int) {
        try {
            equalizer?.release()
            equalizer = Equalizer(0, sessionId).apply { enabled = true }
        } catch (_: Throwable) {
            equalizer = null
        }
    }

    fun playFromUri(uri: String) {
        player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
        player.prepare()
        player.playWhenReady = true
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun play() = player.play()

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs.coerceAtLeast(0L))
        _positionMs.value = player.currentPosition
    }

    fun tick() {
        _positionMs.value = player.currentPosition
        _durationMs.value = if (player.duration > 0) player.duration else 1L
    }

    fun isEqualizerAvailable(): Boolean = equalizer != null

    fun equalizerBandLevelRange(): Pair<Short, Short> {
        val range = equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500)
        return range[0] to range[1]
    }

    fun equalizerBandFrequenciesHz(): List<Int> {
        val eq = equalizer ?: return emptyList()
        return (0 until eq.numberOfBands)
            .map { band -> eq.getCenterFreq(band.toShort()) / 1000 }
    }

    fun equalizerBandLevels(): List<Short> {
        val eq = equalizer ?: return emptyList()
        return (0 until eq.numberOfBands)
            .map { band -> eq.getBandLevel(band.toShort()) }
    }

    fun equalizerPresetNames(): List<String> {
        val eq = equalizer ?: return emptyList()
        return (0 until eq.numberOfPresets).map { idx -> eq.getPresetName(idx.toShort()) }
    }

    fun applyEqualizerPreset(index: Int) {
        val eq = equalizer ?: return
        if (index in 0 until eq.numberOfPresets) {
            eq.usePreset(index.toShort())
        }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
    }

    fun isEqualizerEnabled(): Boolean = equalizer?.enabled == true

    fun setEqualizerBandLevel(band: Int, level: Short) {
        val eq = equalizer ?: return
        if (band in 0 until eq.numberOfBands) {
            eq.setBandLevel(band.toShort(), level)
        }
    }
}
