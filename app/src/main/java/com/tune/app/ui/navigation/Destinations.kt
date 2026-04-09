package com.tune.app.ui.navigation

import android.net.Uri

sealed class Destination(val route: String) {
    data object Splash : Destination("splash")
    data object Home : Destination("home")
    data object NowPlaying : Destination("now_playing")
    data object Playlists : Destination("playlists")
    data object Search : Destination("search")
    data object Artist : Destination("artist/{artistName}") {
        const val ARG_ARTIST = "artistName"
        fun createRoute(artistName: String): String = "artist/${Uri.encode(artistName)}"
    }
    data object Album : Destination("album/{albumName}") {
        const val ARG_ALBUM = "albumName"
        fun createRoute(albumName: String): String = "album/${Uri.encode(albumName)}"
    }
    data object Settings : Destination("settings")
    data object SettingsEqualizer : Destination("settings_equalizer")
    data object SettingsScanMusic : Destination("settings_scan_music")
    data object SettingsScanProgress : Destination("settings_scan_progress")
}
