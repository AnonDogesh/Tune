package com.tune.app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tune.app.ui.navigation.Destination
import com.tune.app.ui.screens.AlbumScreen
import com.tune.app.ui.screens.ArtistScreen
import com.tune.app.ui.screens.EqualizerScreen
import com.tune.app.ui.screens.HomeScreen
import com.tune.app.ui.screens.NowPlayingScreen
import com.tune.app.ui.screens.PlaylistsScreen
import com.tune.app.ui.screens.ScanMusicScreen
import com.tune.app.ui.screens.ScanProgressScreen
import com.tune.app.ui.screens.SearchScreen
import com.tune.app.ui.screens.SettingsScreen
import com.tune.app.ui.screens.SplashScreen
import com.tune.app.ui.components.ClayButton
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.VioletAccent

@Composable
fun TuneApp() {
    val navController = rememberNavController()
    val vm: TuneViewModel = hiltViewModel()
    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val songs by vm.songs.collectAsStateWithLifecycle()

    val items = listOf(
        Triple(Destination.Home, Icons.Default.Home, "Library"),
        Triple(Destination.Playlists, Icons.Default.LibraryMusic, "Playlists"),
        Triple(Destination.Search, Icons.Default.Search, "Search"),
        Triple(Destination.Settings, Icons.Default.Settings, "Settings")
    )

    Scaffold(
        containerColor = OffWhiteBackground,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val current = backStackEntry?.destination
            if (current?.route != Destination.Splash.route) {
                NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                    items.forEach { (dest, icon, label) ->
                        NavigationBarItem(
                            selected = current?.hierarchy?.any { it.route == dest.route } == true,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label, tint = CharcoalText) },
                            label = { Text(label, color = VioletAccent) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val current = backStackEntry?.destination

            NavHost(
                navController = navController,
                startDestination = Destination.Splash.route
            ) {
                composable(Destination.Splash.route) {
                    SplashScreen { navController.navigate(Destination.Home.route) { popUpTo(0) } }
                }
                composable(
                    Destination.Home.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            spring(stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn()
                    },
                    exitTransition = { fadeOut() + slideOutVertically() }
                ) {
                    HomeScreen(vm = vm, onNowPlaying = { navController.navigate(Destination.NowPlaying.route) }, onArtist = {}, onAlbum = {})
                }
                composable(Destination.NowPlaying.route) { NowPlayingScreen(vm = vm, onBack = { navController.popBackStack() }) }
                composable(Destination.Playlists.route) { PlaylistsScreen(vm = vm) }
                composable(Destination.Search.route) {
                    SearchScreen(
                        vm = vm,
                        onNowPlaying = { navController.navigate(Destination.NowPlaying.route) },
                        onArtist = { artist -> navController.navigate(Destination.Artist.createRoute(artist)) }
                    )
                }
                composable(
                    route = Destination.Artist.route,
                    arguments = listOf(navArgument(Destination.Artist.ARG_ARTIST) { defaultValue = "" })
                ) { backStack ->
                    val artistName = backStack.arguments?.getString(Destination.Artist.ARG_ARTIST).orEmpty()
                    ArtistScreen(
                        artistName = artistName,
                        songs = songs,
                        currentSongId = currentSong?.id,
                        onBack = { navController.popBackStack() },
                        onPlaySong = {
                            vm.playSongFromLibrary(it)
                            navController.navigate(Destination.NowPlaying.route)
                        }
                    )
                }
                composable(Destination.Album.route) { AlbumScreen() }
                composable(Destination.Settings.route) {
                    SettingsScreen(
                        onEqualizer = { navController.navigate(Destination.SettingsEqualizer.route) },
                        onScanMusic = { navController.navigate(Destination.SettingsScanMusic.route) },
                        onScanProgress = { navController.navigate(Destination.SettingsScanProgress.route) }
                    )
                }
                composable(Destination.SettingsEqualizer.route) {
                    EqualizerScreen(onBack = { navController.popBackStack() })
                }
                composable(Destination.SettingsScanMusic.route) {
                    ScanMusicScreen(
                        onBack = { navController.popBackStack() },
                        onStartScan = { navController.navigate(Destination.SettingsScanProgress.route) }
                    )
                }
                composable(Destination.SettingsScanProgress.route) {
                    ScanProgressScreen(onStop = { navController.popBackStack() })
                }
            }

            if (currentSong != null && current?.route != Destination.Splash.route && current?.route != Destination.NowPlaying.route) {
                GlassBox(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .height(72.dp)
                        .alpha(1f)
                        .clickable { navController.navigate(Destination.NowPlaying.route) },
                    shadowElevation = 0.dp,
                    shadowColor = Color.Transparent,
                    glassAlpha = 0.30f,
                    borderAlpha = 0.75f,
                    blurAlpha = 0.30f,
                    blurRadius = 36f,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                currentSong?.title.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = CharcoalText
                            )
                            Text(
                                currentSong?.artist.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = VioletAccent
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = vm::previousSong) { Icon(Icons.Default.SkipPrevious, null, tint = VioletAccent) }
                            ClayButton(
                                onClick = vm::togglePlayPause,
                                modifier = Modifier.size(42.dp),
                                baseColor = OliveAccent
                            ) {
                                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White)
                            }
                            IconButton(onClick = vm::nextSong) { Icon(Icons.Default.SkipNext, null, tint = VioletAccent) }
                        }
                    }
                }
            }
        }
    }
}
