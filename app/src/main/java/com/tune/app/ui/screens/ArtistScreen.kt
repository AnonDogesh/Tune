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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tune.app.data.model.Song
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletPale

@Composable
fun ArtistScreen(
    artistName: String,
    songs: List<Song>,
    currentSongId: Long?,
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit
) {
    val artistSongs = songs.filter { it.artist.equals(artistName, true) }
    val albums = artistSongs.groupBy { it.album }

    Box(Modifier.fillMaxSize().background(OffWhiteBackground)) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(VioletPale.copy(alpha = 0.5f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 180.dp, y = 200.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(OlivePale.copy(alpha = 0.4f), Color.Transparent)))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBackIosNew, null, tint = VioletAccent)
                        }
                    }
                    Column {
                        Text("Artist", color = MutedGreyText, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp))
                        Text(artistName.ifBlank { "Unknown Artist" }, color = CharcoalText, style = MaterialTheme.typography.headlineLarge)
                    }
                }
            }

            albums.forEach { (albumName, albumSongs) ->
                item {
                    Text(albumName.ifBlank { "Unknown Album" }, color = OliveAccent, style = MaterialTheme.typography.titleLarge)
                }
                items(albumSongs, key = { it.id }) { song ->
                    GlassBox(
                        modifier = Modifier.fillMaxWidth().clickable { onPlaySong(song) },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = song.albumArtUri,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(VioletPale)
                            )
                            Column(Modifier.weight(1f)) {
                                Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                                Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MutedGreyText, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(song.duration, color = MutedGreyText, style = MaterialTheme.typography.labelLarge)
                        }
                        if (currentSongId == song.id) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .border(2.dp, VioletAccent, RoundedCornerShape(20.dp))
                            )
                        }
                    }
                }
            }

            if (artistSongs.isEmpty()) {
                item {
                    GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(16.dp)) {
                        Text("No songs found for this artist.", color = MutedGreyText)
                    }
                }
            }
        }
    }
}
