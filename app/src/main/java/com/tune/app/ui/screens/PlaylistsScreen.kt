package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tune.app.ui.state.TuneViewModel

@Composable
fun PlaylistsScreen(vm: TuneViewModel) {
    val playlists by vm.playlists.collectAsStateWithLifecycle()
    var selected by remember(playlists) { mutableStateOf(playlists.firstOrNull() ?: "Favorites (0)") }
    val songs = vm.getPlaylistSongs(selected)
    var showCreate by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Playlists", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = { showCreate = true }) { Text("Create Playlist") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            playlists.forEach { p ->
                FilterChip(selected = p == selected, onClick = { selected = p }, label = { Text(p) })
            }
        }

        Text("Tracks", style = MaterialTheme.typography.titleLarge)
        if (songs.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Text("No songs in this playlist")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(songs) { song ->
                    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().clickable { vm.playSongFromPlaylist(selected, song) }) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(song.duration, style = MaterialTheme.typography.labelLarge)
                            TextButton(onClick = { vm.removeSongFromPlaylist(selected, song.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "remove")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("New Playlist") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Playlist name") }) },
            confirmButton = {
                TextButton(onClick = {
                    vm.createPlaylist(name.trim())
                    name = ""
                    showCreate = false
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreate = false }) { Text("Cancel") } }
        )
    }
}
