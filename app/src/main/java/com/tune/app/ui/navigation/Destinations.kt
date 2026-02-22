package com.tune.app.ui.navigation

sealed class Destination(val route: String) {
    data object Splash : Destination("splash")
    data object Home : Destination("home")
    data object NowPlaying : Destination("now_playing")
    data object Playlists : Destination("playlists")
    data object Search : Destination("search")
    data object Artist : Destination("artist")
    data object Album : Destination("album")
    data object Settings : Destination("settings")
    data object SettingsEqualizer : Destination("settings_equalizer")
    data object SettingsScanMusic : Destination("settings_scan_music")
    data object SettingsScanProgress : Destination("settings_scan_progress")
}
