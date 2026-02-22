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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CoralRed
import com.tune.app.ui.theme.DeepTeal
import com.tune.app.ui.theme.SeaGreen

@Composable
fun NowPlayingScreen(vm: TuneViewModel) {
    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val positionMs by vm.positionMs.collectAsStateWithLifecycle()
    val durationMs by vm.durationMs.collectAsStateWithLifecycle()
    val queueName by vm.queueName.collectAsStateWithLifecycle()
    val queueSongs by vm.queueSongs.collectAsStateWithLifecycle()
    val shuffleEnabled by vm.isShuffleEnabled.collectAsStateWithLifecycle()
    val repeatMode by vm.currentRepeatMode.collectAsStateWithLifecycle()
    val customPlaylists by vm.customPlaylistNames.collectAsStateWithLifecycle()

    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    var menuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DeepTeal).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(modifier = Modifier.background(Color.White.copy(0.08f), CircleShape), onClick = {}) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING FROM", color = Color.White.copy(0.5f), fontWeight = FontWeight.SemiBold)
                    Text(queueName, color = Color.White)
                }
                Box {
                    IconButton(modifier = Modifier.background(Color.White.copy(0.08f), CircleShape), onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        customPlaylists.forEach { playlist ->
                            DropdownMenuItem(text = { Text("Add to $playlist") }, onClick = {
                                vm.addCurrentSongToPlaylist(playlist)
                                menuExpanded = false
                            })
                        }
                        DropdownMenuItem(text = { Text("Song details") }, onClick = { menuExpanded = false })
                    }
                }
            }
        }

        item {
            Box(
                Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(40.dp)).background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = currentSong?.albumArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.78f).height(250.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFEDE8DC))
                )
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(currentSong?.title ?: "Pick a song", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color(0xFFE9C46A), style = MaterialTheme.typography.headlineLarge)
                    Text(currentSong?.artist ?: "Unknown Artist", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White.copy(0.6f), style = MaterialTheme.typography.titleLarge)
                }
                currentSong?.let { song ->
                    val fav = song.id in favorites
                    IconButton(onClick = { vm.toggleFavorite(song.id) }) {
                        Icon(if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "favorite", tint = if (fav) CoralRed else Color(0xFFD39A5D), modifier = Modifier.size(40.dp))
                    }
                }
            }
        }

        item { Slider(value = progress, onValueChange = vm::seekToFraction, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMs(positionMs), color = Color.White.copy(0.4f))
                Text(formatMs(durationMs), color = Color.White.copy(0.4f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = vm::toggleShuffle) { Icon(Icons.Default.SwapHoriz, null, tint = if (shuffleEnabled) SeaGreen else Color(0xFFD39A5D)) }
                IconButton(onClick = vm::previousSong) { Icon(Icons.Default.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(42.dp)) }
                IconButton(modifier = Modifier.size(100.dp).background(SeaGreen, CircleShape), onClick = vm::togglePlayPause) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(52.dp))
                }
                IconButton(onClick = vm::nextSong) { Icon(Icons.Default.SkipNext, null, tint = Color.White, modifier = Modifier.size(42.dp)) }
                IconButton(onClick = vm::cycleRepeatMode) {
                    val icon = if (repeatMode == TuneViewModel.RepeatMode.One) Icons.Default.RepeatOne else Icons.Default.Repeat
                    Icon(icon, contentDescription = "repeat", tint = if (repeatMode == TuneViewModel.RepeatMode.Off) Color(0xFFD39A5D) else SeaGreen)
                }
            }
        }

        item { Text("Up Next", color = Color.White.copy(0.8f), style = MaterialTheme.typography.titleLarge) }
        items(queueSongs) { song ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.06f)).clickable { vm.playFromQueue(song) }.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(model = song.albumArtUri, contentDescription = null, modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Gray))
                Column(Modifier.weight(1f)) {
                    Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
                    Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
                }
                Text(song.duration, color = Color.White.copy(0.6f))
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
