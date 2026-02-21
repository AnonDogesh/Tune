package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.DeepTeal

@Composable
fun SearchScreen(vm: TuneViewModel, onNowPlaying: () -> Unit) {
    val songs by vm.filteredSongs.collectAsStateWithLifecycle()
    val query by vm.searchQuery.collectAsStateWithLifecycle()
    val recent by vm.recentSearches.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(DeepTeal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Search", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        OutlinedTextField(
            value = query,
            onValueChange = vm::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(40.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2A9D8F)) },
            placeholder = { Text("Artists, songs, or podcasts", color = Color(0xFF2A9D8F)) }
        )

        Text("RECENT SEARCHES", color = Color(0xFF2A9D8F), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            recent.forEach { item ->
                AssistChip(
                    onClick = { vm.setSearchQuery(item) },
                    label = { Text(item) },
                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Text("Browse Categories", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        val categories = listOf("Artists" to Color(0xFF2A9D8F), "Albums" to Color(0xFFF4A261), "Genres" to Color(0xFFE9C46A), "Podcasts" to Color(0xFF7485A5))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(220.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories) { (name, color) ->
                androidx.compose.material3.Card(shape = RoundedCornerShape(40.dp), modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    Row(Modifier.fillMaxSize().background(color).padding(16.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, style = MaterialTheme.typography.headlineLarge, color = Color.White)
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Top Tracks", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Text("VIEW ALL", color = Color(0xFF2A9D8F))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(songs.take(10)) { song ->
                Row(
                    Modifier.fillMaxWidth().background(Color.Transparent, RoundedCornerShape(14.dp)).padding(4.dp).clickable {
                        vm.playSongFromLibrary(song)
                        onNowPlaying()
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(model = song.albumArtUri, contentDescription = null, modifier = Modifier.size(56.dp).background(Color.Black, CircleShape))
                    Column(Modifier.weight(1f)) {
                        Text(song.title, color = Color.White)
                        Text("${song.artist} • ${song.album}", color = Color(0xFF2A9D8F))
                    }
                    Icon(Icons.Default.MoreVert, null, tint = Color(0xFF2A9D8F))
                }
            }
        }
    }
}
