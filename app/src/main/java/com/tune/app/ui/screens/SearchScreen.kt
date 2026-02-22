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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.tune.app.data.model.Song
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.DeepTeal

private enum class SearchCategory(val label: String, val placeholder: String) {
    Artists("Artists", "Filter artists..."),
    Albums("Albums", "Filter albums..."),
    Genres("Genres", "Filter genres..."),
    Podcasts("Podcasts", "Filter podcasts...")
}

@Composable
fun SearchScreen(vm: TuneViewModel, onNowPlaying: () -> Unit) {
    val songs by vm.songs.collectAsStateWithLifecycle()
    val query by vm.searchQuery.collectAsStateWithLifecycle()
    val recent by vm.recentSearches.collectAsStateWithLifecycle()
    var category by remember { mutableStateOf<SearchCategory?>(null) }

    if (category != null) {
        CategoryScreen(category = category!!, songs = songs, onBack = { category = null }, onPlaySong = {
            vm.playSongFromLibrary(it)
            onNowPlaying()
        })
        return
    }

    val artistResults = songs.map { it.artist }.distinct().filter { it.contains(query, true) }
    val albumResults = songs.map { it.album }.distinct().filter { it.contains(query, true) }
    val songResults = songs.filter { it.title.contains(query, true) || it.artist.contains(query, true) }

    Column(
        Modifier
            .fillMaxSize()
            .background(DeepTeal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Search", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            if (query.isNotBlank()) {
                Text("Cancel", color = Color(0xFFE9C46A), modifier = Modifier.clickable { vm.setSearchQuery("") })
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = vm::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(40.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2A9D8F)) },
            trailingIcon = {
                if (query.isNotBlank()) IconButton(onClick = { vm.setSearchQuery("") }) {
                    Icon(Icons.Default.Cancel, null, tint = Color(0xFF2A9D8F))
                }
            },
            placeholder = { Text("Artists, songs, or podcasts", color = Color(0xFF2A9D8F)) }
        )

        if (query.isBlank()) {
            Text("RECENT SEARCHES", color = Color(0xFF2A9D8F), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recent.forEach { item ->
                    AssistChip(onClick = { vm.setSearchQuery(item) }, label = { Text(item) })
                }
            }

            Text("Browse Categories", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            val categories = listOf(
                SearchCategory.Artists to Color(0xFF2A9D8F),
                SearchCategory.Albums to Color(0xFFF4A261),
                SearchCategory.Genres to Color(0xFFE9C46A),
                SearchCategory.Podcasts to Color(0xFF7485A5)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories.chunked(2)) { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { (item, color) ->
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(34.dp))
                                    .background(color)
                                    .clickable { category = item }
                                    .padding(16.dp)
                            ) {
                                Text(item.label, style = MaterialTheme.typography.headlineLarge, color = Color.White)
                            }
                        }
                        if (rowItems.size == 1) Row(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        } else {
            Text("TOP RESULTS", color = Color(0xFF2A9D8F), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (artistResults.isNotEmpty()) {
                    item {
                        ResultRow(
                            title = artistResults.first(),
                            subtitle = "Artist",
                            onClick = { category = SearchCategory.Artists },
                            trailing = { Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF2A9D8F)) }
                        )
                    }
                }
                if (albumResults.isNotEmpty()) {
                    item {
                        ResultRow(
                            title = albumResults.first(),
                            subtitle = "Album • Artist Name",
                            onClick = { category = SearchCategory.Albums },
                            trailing = { Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF2A9D8F)) }
                        )
                    }
                }
                items(songResults.take(20)) { song ->
                    ResultRow(
                        title = song.title,
                        subtitle = "Song • ${song.artist}",
                        onClick = {
                            vm.playSongFromLibrary(song)
                            onNowPlaying()
                        },
                        art = song.albumArtUri,
                        trailing = { Icon(Icons.Default.MoreVert, null, tint = Color(0xFF2A9D8F)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryScreen(
    category: SearchCategory,
    songs: List<Song>,
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit
) {
    var filter by remember { mutableStateOf("") }
    val rows = when (category) {
        SearchCategory.Artists -> songs.map { it.artist }.distinct().filter { it.contains(filter, true) }.sorted()
        SearchCategory.Albums -> songs.map { it.album }.distinct().filter { it.contains(filter, true) }.sorted()
        SearchCategory.Genres -> songs.map { it.album }.distinct().filter { it.contains(filter, true) }.sorted() // fallback metadata
        SearchCategory.Podcasts -> emptyList()
    }

    Column(Modifier.fillMaxSize().background(DeepTeal).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, tint = Color(0xFFE9C46A)) }
            Text(category.label, style = MaterialTheme.typography.headlineLarge, color = Color(0xFFE9C46A))
        }

        OutlinedTextField(
            value = filter,
            onValueChange = { filter = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(40.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2A9D8F)) },
            placeholder = { Text(category.placeholder, color = Color(0xFF2A9D8F)) }
        )

        if (category == SearchCategory.Podcasts) {
            Text("No podcast index available offline yet.", color = Color.White)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(rows) { name ->
                    val art = songs.firstOrNull {
                        when (category) {
                            SearchCategory.Artists -> it.artist == name
                            SearchCategory.Albums, SearchCategory.Genres -> it.album == name
                            SearchCategory.Podcasts -> false
                        }
                    }?.albumArtUri

                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable {
                            songs.firstOrNull {
                                when (category) {
                                    SearchCategory.Artists -> it.artist == name
                                    SearchCategory.Albums, SearchCategory.Genres -> it.album == name
                                    SearchCategory.Podcasts -> false
                                }
                            }?.let(onPlaySong)
                        }.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(model = art, contentDescription = null, modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black))
                        Text(name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    art: String? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(model = art, contentDescription = null, modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black))
        Column(Modifier.fillMaxWidth(0.68f)) {
            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
            Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color(0xFFE9C46A))
        }
        trailing()
    }
}
