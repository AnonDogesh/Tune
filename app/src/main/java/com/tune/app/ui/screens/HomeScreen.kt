package com.tune.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tune.app.ui.state.TuneViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    vm: TuneViewModel,
    onNowPlaying: () -> Unit,
    onArtist: () -> Unit,
    onAlbum: () -> Unit
) {
    val songs by vm.songs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) vm.refreshLibrary()
    }

    LaunchedEffect(granted) {
        if (granted) vm.refreshLibrary() else launcher.launch(permission)
    }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Tune", style = MaterialTheme.typography.headlineLarge)
            }

            if (!granted) {
                item {
                    Card(shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Audio permission required", style = MaterialTheme.typography.titleLarge)
                            Text("Allow audio access to scan and play your offline songs.")
                            Button(onClick = { launcher.launch(permission) }) { Text("Grant permission") }
                        }
                    }
                }
            }

            if (songs.isNotEmpty()) {
                item {
                    Text("Recently Played", style = MaterialTheme.typography.titleLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 8.dp)) {
                        items(songs.take(6)) { song ->
                            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(Modifier.padding(12.dp).clickable { vm.playSong(song); onNowPlaying() }) {
                                    Box(Modifier.size(130.dp).background(MaterialTheme.colorScheme.primary.copy(0.2f), RoundedCornerShape(20.dp)))
                                    Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }

            stickyHeader {
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("All Songs", style = MaterialTheme.typography.titleLarge)
                    Icon(Icons.Default.Refresh, contentDescription = "refresh", modifier = Modifier.rotate(12f))
                }
            }

            if (songs.isEmpty()) {
                item { Text("No songs found yet. Pull to refresh after granting permission.") }
            } else {
                items(songs) { song ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { vm.playSong(song); onNowPlaying() }, shape = RoundedCornerShape(20.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(Modifier.size(56.dp).background(MaterialTheme.colorScheme.secondary.copy(0.25f), RoundedCornerShape(16.dp)))
                            Column(Modifier.weight(1f)) {
                                Text(song.title)
                                Text(song.artist, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(song.duration, style = MaterialTheme.typography.labelLarge)
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                songs.firstOrNull()?.let { vm.playSong(it); onNowPlaying() }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.tertiary
        ) { Icon(Icons.Default.Shuffle, contentDescription = "shuffle all") }
    }
}
