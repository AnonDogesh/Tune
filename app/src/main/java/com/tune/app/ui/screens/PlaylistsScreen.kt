package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tune.app.ui.components.ClayButton
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.SurfaceWhite
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletDark
import com.tune.app.ui.theme.VioletLight
import com.tune.app.ui.theme.VioletPale

@Composable
fun PlaylistsScreen(vm: TuneViewModel) {
    val playlists by vm.playlists.collectAsStateWithLifecycle()
    var selected by remember(playlists) { mutableStateOf(playlists.firstOrNull() ?: "Favorites (0)") }
    val songs = vm.getPlaylistSongs(selected)
    var showCreate by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Box(
        Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(Brush.radialGradient(listOf(VioletPale.copy(alpha = 0.5f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 180.dp, y = 200.dp)
                .background(Brush.radialGradient(listOf(com.tune.app.ui.theme.OlivePale.copy(alpha = 0.4f), Color.Transparent)), CircleShape)
        )

        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Playlists", style = MaterialTheme.typography.headlineLarge, color = CharcoalText)
        ClayButton(onClick = { showCreate = true }, shape = RoundedCornerShape(20.dp), baseColor = OliveAccent) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("New", color = Color.White)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            playlists.forEach { p ->
                if (p == selected) {
                    ClaySurface(
                        modifier = Modifier.clickable { selected = p },
                        shape = RoundedCornerShape(16.dp),
                        baseColor = VioletAccent,
                        brush = Brush.linearGradient(listOf(VioletLight, VioletDark))
                    ) {
                        Text(p, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                } else {
                    GlassBox(
                        modifier = Modifier.clickable { selected = p },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Box(Modifier.border(1.dp, VioletPale, RoundedCornerShape(16.dp)).padding(horizontal = 2.dp, vertical = 0.dp)) {
                            Text(p, color = CharcoalText)
                        }
                    }
                }
            }
        }

        Text("Tracks", style = MaterialTheme.typography.titleLarge, color = CharcoalText)
        if (songs.isEmpty()) {
            GlassBox(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("✦", color = VioletPale)
                    Text("No songs in this playlist", color = MutedGreyText)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(songs) { song ->
                    GlassBox(
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth().clickable { vm.playSongFromPlaylist(selected, song) },
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                                Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = OliveAccent)
                            }
                            Text(song.duration, style = MaterialTheme.typography.labelLarge, color = MutedGreyText)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFFF0F2), CircleShape)
                                    .clickable { vm.removeSongFromPlaylist(selected, song.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "remove", tint = Color(0xFFD4686F))
                            }
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
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(32.dp),
            title = { Text("New Playlist") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Playlist name") }) },
            confirmButton = {
                ClayButton(onClick = {
                    vm.createPlaylist(name.trim())
                    name = ""
                    showCreate = false
                }, baseColor = VioletAccent, shape = RoundedCornerShape(16.dp)) { Text("Create", color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) }
            },
            dismissButton = { TextButton(onClick = { showCreate = false }) { Text("Cancel", color = MutedGreyText) } }
        )
    }
}
