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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.CoralRed
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OliveMist
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.SurfaceWhite
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletLight
import com.tune.app.ui.theme.VioletMist
import com.tune.app.ui.theme.VioletPale

@Composable
fun NowPlayingScreen(vm: TuneViewModel, onBack: () -> Unit) {
    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val positionMs by vm.positionMs.collectAsStateWithLifecycle()
    val durationMs by vm.durationMs.collectAsStateWithLifecycle()
    val queueName by vm.queueName.collectAsStateWithLifecycle()
    val queueSongs by vm.queueSongs.collectAsStateWithLifecycle()
    val repeatMode by vm.currentRepeatMode.collectAsStateWithLifecycle()
    val customPlaylists by vm.customPlaylistNames.collectAsStateWithLifecycle()

    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    var menuExpanded by remember { mutableStateOf(false) }
    var showSongDetails by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = listOf(VioletPale.copy(alpha = 0.5f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 180.dp, y = 200.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = listOf(OlivePale.copy(alpha = 0.4f), Color.Transparent)))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = VioletAccent)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NOW PLAYING FROM", color = MutedGreyText, fontWeight = FontWeight.SemiBold)
                        Text(queueName, color = CharcoalText)
                    }
                    Box {
                        GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null, tint = VioletAccent)
                            }
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            customPlaylists.forEach { playlist ->
                                DropdownMenuItem(text = { Text("Add to $playlist") }, onClick = {
                                    vm.addCurrentSongToPlaylist(playlist)
                                    menuExpanded = false
                                })
                            }
                            DropdownMenuItem(text = { Text("Song details") }, onClick = {
                                menuExpanded = false
                                showSongDetails = true
                            })
                        }
                    }
                }
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Brush.linearGradient(listOf(VioletMist, OliveMist)))
                        .border(1.5.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.78f).height(250.dp).clip(RoundedCornerShape(6.dp)).background(VioletMist)
                    )
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(currentSong?.title ?: "Pick a song", maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText, style = MaterialTheme.typography.headlineLarge)
                        Text(currentSong?.artist ?: "Unknown Artist", maxLines = 1, overflow = TextOverflow.Ellipsis, color = OliveAccent, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            item {
                Slider(
                    value = progress,
                    onValueChange = vm::seekToFraction,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = VioletAccent,
                        activeTrackColor = VioletAccent,
                        inactiveTrackColor = VioletPale
                    )
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatMs(positionMs), color = MutedGreyText)
                    Text(formatMs(durationMs), color = MutedGreyText)
                }
            }
            item {
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(36.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        currentSong?.let { song ->
                            val fav = song.id in favorites
                            IconButton(
                                modifier = Modifier.clip(CircleShape).background(if (fav) OliveMist else Color.Transparent),
                                onClick = { vm.toggleFavorite(song.id) }
                            ) {
                                Icon(
                                    if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    null,
                                    tint = if (fav) CoralRed else MutedGreyText
                                )
                            }
                        } ?: IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, null, tint = MutedGreyText) }
                        GlassBox(modifier = Modifier.size(48.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                            IconButton(onClick = vm::previousSong) { Icon(Icons.Default.SkipPrevious, null, tint = CharcoalText) }
                        }
                        ClaySurface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            baseColor = VioletAccent,
                            brush = Brush.linearGradient(listOf(VioletLight, VioletAccent))
                        ) {
                            IconButton(onClick = vm::togglePlayPause) {
                                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = SurfaceWhite, modifier = Modifier.size(42.dp))
                            }
                        }
                        GlassBox(modifier = Modifier.size(48.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                            IconButton(onClick = vm::nextSong) { Icon(Icons.Default.SkipNext, null, tint = CharcoalText) }
                        }
                        IconButton(
                            modifier = Modifier.clip(CircleShape).background(if (repeatMode != TuneViewModel.RepeatMode.Off) OliveMist else Color.Transparent),
                            onClick = vm::cycleRepeatMode
                        ) {
                            val icon = if (repeatMode == TuneViewModel.RepeatMode.One) Icons.Default.RepeatOne else Icons.Default.Repeat
                            Icon(icon, contentDescription = "repeat", tint = if (repeatMode == TuneViewModel.RepeatMode.Off) MutedGreyText else OliveAccent)
                        }
                    }
                }
            }

            item { Text("Up Next", color = CharcoalText, style = MaterialTheme.typography.titleLarge) }
            items(queueSongs) { song ->
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.playFromQueue(song) },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(model = song.albumArtUri, contentDescription = null, modifier = Modifier.size(44.dp).clip(CircleShape).background(VioletMist))
                        Column(Modifier.weight(1f)) {
                            Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                            Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MutedGreyText, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(song.duration, color = MutedGreyText)
                    }
                    if (currentSong?.id == song.id) {
                        Box(Modifier.fillMaxSize().border(2.dp, VioletAccent, RoundedCornerShape(20.dp)))
                    }
                }
            }
        }
    }

    if (showSongDetails) {
        Dialog(onDismissRequest = { showSongDetails = false }) {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(18.dp),
                shadowElevation = 0.dp,
                glassAlpha = 0.55f,
                blurAlpha = 0.2f,
                blurRadius = 24f
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Song Details", color = CharcoalText, style = MaterialTheme.typography.titleLarge)
                    DetailRow("Title", currentSong?.title.orEmpty())
                    DetailRow("Artist", currentSong?.artist.orEmpty())
                    DetailRow("Album", currentSong?.album.orEmpty())
                    DetailRow("Duration", currentSong?.duration.orEmpty())
                    DetailRow("File path", currentSong?.path.orEmpty())
                    DetailRow("Album art URI", currentSong?.albumArtUri.orEmpty())
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, color = OliveAccent, style = MaterialTheme.typography.labelSmall)
        Text(value.ifBlank { "—" }, color = CharcoalText, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
