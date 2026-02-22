# Tune 🎵

Tune is an offline Android music player built with **Kotlin**, **Jetpack Compose**, **Material 3**, and **Media3**.

## Included in this initial version

- Elegant theme with the requested palette and rounded shape language.
- Multi-screen navigation: Splash, Home/Library, Now Playing, Playlists, Search, Artist, Album, Settings.
- Animated interactions (pulse splash, bouncy controls, visualizer bars, transitions).
- Foundation architecture for offline playback + library management using Media3, Room, Hilt, DataStore-ready modules.

## Stack

- Kotlin + Jetpack Compose
- Material 3
- Navigation Compose
- Hilt
- Media3 ExoPlayer
- Room + DataStore

## Next implementation steps

1. Connect MediaStore scanning to repository refresh.
2. Add foreground playback service with MediaSession + notifications.
3. Build real lyrics parser and waveform from FFT.
4. Add widgets, backup/restore flow, and tag editor.
