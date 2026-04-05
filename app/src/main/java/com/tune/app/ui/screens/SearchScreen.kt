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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletPale

@Composable
fun SearchScreen(vm: TuneViewModel, onNowPlaying: () -> Unit, onArtist: (String) -> Unit) {
    val songs by vm.songs.collectAsStateWithLifecycle()
    val query by vm.searchQuery.collectAsStateWithLifecycle()

    val normalized = query.trim()
    val hasQuery = normalized.isNotEmpty()
    val artistResults = if (hasQuery) songs.map { it.artist }.distinct().filter { it.contains(normalized, true) } else emptyList()
    val albumResults = if (hasQuery) songs.map { it.album }.distinct().filter { it.contains(normalized, true) } else emptyList()
    val songResults = if (hasQuery) songs.filter { it.title.contains(normalized, true) } else emptyList()

    Box(
        Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
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

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Search", style = MaterialTheme.typography.headlineLarge, color = CharcoalText)

            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = vm::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = VioletAccent) },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { vm.setSearchQuery("") }) {
                                Icon(Icons.Default.Cancel, null, tint = OliveAccent)
                            }
                        }
                    },
                    placeholder = { Text("Search artists, albums, songs", color = MutedGreyText) },
                    singleLine = true
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 190.dp)
            ) {
                if (hasQuery && artistResults.isNotEmpty()) {
                    item { SectionHeader("Artists") }
                    items(artistResults) { artist ->
                        ResultRow(title = artist, subtitle = "Artist", onClick = { onArtist(artist) })
                    }
                }

                if (hasQuery && albumResults.isNotEmpty()) {
                    item { SectionHeader("Albums") }
                    items(albumResults) { album ->
                        val art = songs.firstOrNull { it.album == album }?.albumArtUri
                        val albumArtist = songs.firstOrNull { it.album == album }?.artist.orEmpty()
                        ResultRow(title = album, subtitle = "Album", art = art, onClick = { onArtist(albumArtist) })
                    }
                }

                if (hasQuery && songResults.isNotEmpty()) {
                    item { SectionHeader("Songs") }
                    items(songResults) { song ->
                        ResultRow(
                            title = song.title,
                            subtitle = song.artist,
                            art = song.albumArtUri,
                            onClick = {
                                vm.playSongFromLibrary(song)
                                onNowPlaying()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, color = OliveAccent, style = MaterialTheme.typography.labelSmall)
}

@Composable
private fun ResultRow(
    title: String,
    subtitle: String,
    art: String? = null,
    onClick: () -> Unit = {}
) {
    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = art,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(VioletPale)
            )
            Column(Modifier.weight(1f)) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MutedGreyText)
            }
        }
    }
}
