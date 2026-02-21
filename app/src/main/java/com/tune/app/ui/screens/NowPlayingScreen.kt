package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepTeal)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(modifier = Modifier.background(Color.White.copy(0.08f), CircleShape), onClick = {}) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING FROM", color = Color.White.copy(0.5f), fontWeight = FontWeight.SemiBold)
                Text("Favorites Playlist", color = Color.White)
            }
            IconButton(modifier = Modifier.background(Color.White.copy(0.08f), CircleShape), onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.78f)
                    .height(250.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFEDE8DC), Color(0xFFE7E4D8))))
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(currentSong?.title ?: "Pick a song", color = Color(0xFFE9C46A), style = MaterialTheme.typography.headlineLarge)
            Text(currentSong?.artist ?: "Unknown Artist", color = Color.White.copy(0.6f), style = MaterialTheme.typography.titleLarge)
        }

        Slider(
            value = progress,
            onValueChange = vm::seekToFraction,
            modifier = Modifier.fillMaxWidth(),
            thumb = { Box(Modifier.size(0.dp)) },
            track = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(100))
                        .background(Color.White.copy(0.12f))
                )
                Box(
                    Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(100))
                        .background(CoralRed)
                )
            }
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatMs(positionMs), color = Color.White.copy(0.4f))
            Text(formatMs(durationMs), color = Color.White.copy(0.4f))
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) { Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFFD39A5D)) }
            IconButton(onClick = vm::previousSong) { Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp)) }
            IconButton(
                modifier = Modifier.size(100.dp).background(SeaGreen, CircleShape),
                onClick = vm::togglePlayPause
            ) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
            }
            IconButton(onClick = vm::nextSong) { Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp)) }
            currentSong?.let { song ->
                val fav = song.id in favorites
                IconButton(onClick = { vm.toggleFavorite(song.id) }) {
                    Icon(if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "favorite", tint = if (fav) CoralRed else Color(0xFFD39A5D))
                }
            } ?: IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFFD39A5D)) }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
