package com.tune.app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tune.app.ui.navigation.Destination
import com.tune.app.ui.screens.AlbumScreen
import com.tune.app.ui.screens.ArtistScreen
import com.tune.app.ui.screens.HomeScreen
import com.tune.app.ui.screens.NowPlayingScreen
import com.tune.app.ui.screens.PlaylistsScreen
import com.tune.app.ui.screens.SearchScreen
import com.tune.app.ui.screens.SettingsScreen
import com.tune.app.ui.screens.SplashScreen
import com.tune.app.ui.state.TuneViewModel

@Composable
fun TuneApp() {
    val navController = rememberNavController()
    val vm: TuneViewModel = hiltViewModel()
    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()

    val items = listOf(
        Triple(Destination.Home, Icons.Default.Home, "Library"),
        Triple(Destination.Playlists, Icons.Default.LibraryMusic, "Playlists"),
        Triple(Destination.Search, Icons.Default.Search, "Search"),
        Triple(Destination.Settings, Icons.Default.Settings, "Settings")
    )

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val current = backStackEntry?.destination
            if (current?.route != Destination.Splash.route) {
                Column {
                    if (current?.route != Destination.NowPlaying.route && currentSong != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable { navController.navigate(Destination.NowPlaying.route) },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(androidx.compose.ui.graphics.Color(0xFF1D3F48))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(currentSong?.title.orEmpty(), color = androidx.compose.ui.graphics.Color.White)
                                    Text("${currentSong?.artist} • NOW PLAYING", color = androidx.compose.ui.graphics.Color(0xFF2A9D8F))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = vm::previousSong) { Icon(Icons.Default.SkipPrevious, null, tint = androidx.compose.ui.graphics.Color.White) }
                                    IconButton(modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xFF2A9D8F), CircleShape).size(44.dp), onClick = vm::togglePlayPause) {
                                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = androidx.compose.ui.graphics.Color.White)
                                    }
                                    IconButton(onClick = vm::nextSong) { Icon(Icons.Default.SkipNext, null, tint = androidx.compose.ui.graphics.Color.White) }
                                }
                            }
                        }
                    }
                    NavigationBar {
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
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Splash.route,
            modifier = Modifier.padding(innerPadding)
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
            composable(Destination.NowPlaying.route) { NowPlayingScreen(vm = vm) }
            composable(Destination.Playlists.route) { PlaylistsScreen(vm = vm) }
            composable(Destination.Search.route) { SearchScreen(vm = vm, onNowPlaying = { navController.navigate(Destination.NowPlaying.route) }) }
            composable(Destination.Artist.route) { ArtistScreen() }
            composable(Destination.Album.route) { AlbumScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
