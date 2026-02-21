package com.tune.app.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }

    fun play(item: MediaItem) {
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun pause() = player.pause()
}
