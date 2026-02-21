package com.tune.app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@Composable
fun TuneApp() {
    val navController = rememberNavController()
    val items = listOf(
        Destination.Home to Icons.Default.Home,
        Destination.Playlists to Icons.Default.LibraryMusic,
        Destination.Search to Icons.Default.Search,
        Destination.Settings to Icons.Default.Settings
    )

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val current = backStackEntry?.destination
            if (current?.route != Destination.Splash.route) {
                NavigationBar {
                    items.forEach { (dest, icon) ->
                        NavigationBarItem(
                            selected = current?.hierarchy?.any { it.route == dest.route } == true,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = dest.route) },
                            label = { Text(dest.route.replaceFirstChar { it.uppercase() }) }
                        )
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
                HomeScreen(
                    onNowPlaying = { navController.navigate(Destination.NowPlaying.route) },
                    onArtist = { navController.navigate(Destination.Artist.route) },
                    onAlbum = { navController.navigate(Destination.Album.route) }
                )
            }
            composable(Destination.NowPlaying.route) { NowPlayingScreen() }
            composable(Destination.Playlists.route) { PlaylistsScreen() }
            composable(Destination.Search.route) { SearchScreen() }
            composable(Destination.Artist.route) { ArtistScreen() }
            composable(Destination.Album.route) { AlbumScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
