package com.tune.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import coil.compose.AsyncImage
import com.tune.app.data.model.Song
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OliveDark
import com.tune.app.ui.theme.OliveLight
import com.tune.app.ui.theme.OliveMist
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletLight
import com.tune.app.ui.theme.VioletPale

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Search", style = MaterialTheme.typography.headlineLarge, color = CharcoalText)
            if (query.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OliveMist)
                        .clickable { vm.setSearchQuery("") }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Cancel", color = VioletAccent)
                }
            }
        }
        GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(0.dp)) {
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
                    if (query.isNotBlank()) IconButton(onClick = { vm.setSearchQuery("") }) {
                        Icon(Icons.Default.Cancel, null, tint = OliveAccent)
                    }
                },
                placeholder = { Text("Artists, songs, or podcasts", color = MutedGreyText) }
            )
        }

        if (query.isBlank()) {
            Text("RECENT SEARCHES", color = OliveAccent, style = MaterialTheme.typography.labelSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recent.forEach { item ->
                    GlassBox(
                        modifier = Modifier.clickable { vm.setSearchQuery(item) },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(item, color = CharcoalText)
                    }
                }
            }

            Text("Browse Categories", style = MaterialTheme.typography.headlineLarge, color = CharcoalText)
            val categories = listOf(
                SearchCategory.Artists to Brush.linearGradient(listOf(OliveLight, OliveDark)),
                SearchCategory.Albums to Brush.linearGradient(listOf(Color(0xFFB87333), Color(0xFF8B5020))),
                SearchCategory.Genres to Brush.linearGradient(listOf(VioletLight, VioletAccent)),
                SearchCategory.Podcasts to Brush.linearGradient(listOf(Color(0xFF7A9E9F), Color(0xFF4A7172)))
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories.chunked(2)) { rowItems: List<Pair<SearchCategory, Brush>> ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { (item, brush) ->
                            ClaySurface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clickable { category = item }
                                    .padding(0.dp),
                                shape = RoundedCornerShape(34.dp),
                                baseColor = OliveAccent,
                                brush = brush
                            ) {
                                Row(Modifier.fillMaxSize().padding(16.dp)) {
                                    Text(item.label, style = MaterialTheme.typography.headlineLarge, color = Color.White)
                                }
                            }
                        }
                        if (rowItems.size == 1) Row(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        } else {
            Text("TOP RESULTS", color = OliveAccent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (artistResults.isNotEmpty()) {
                    item {
                        ResultRow(
                            title = artistResults.first(),
                            subtitle = "Artist",
                            onClick = { category = SearchCategory.Artists },
                            trailing = { Icon(Icons.Default.ChevronRight, null, tint = MutedGreyText) }
                        )
                    }
                }
                if (albumResults.isNotEmpty()) {
                    item {
                        ResultRow(
                            title = albumResults.first(),
                            subtitle = "Album • Artist Name",
                            onClick = { category = SearchCategory.Albums },
                            trailing = { Icon(Icons.Default.ChevronRight, null, tint = MutedGreyText) }
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
                        trailing = { Icon(Icons.Default.MoreVert, null, tint = MutedGreyText) }
                    )
                }
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
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, tint = VioletAccent) }
            }
            Text(category.label, style = MaterialTheme.typography.headlineLarge, color = CharcoalText)
        }

        GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(0.dp)) {
            OutlinedTextField(
                value = filter,
                onValueChange = { filter = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = VioletAccent) },
                placeholder = { Text(category.placeholder, color = MutedGreyText) }
            )
        }

        if (category == SearchCategory.Podcasts) {
            Text("No podcast index available offline yet.", color = CharcoalText)
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

                    GlassBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                songs.firstOrNull {
                                    when (category) {
                                        SearchCategory.Artists -> it.artist == name
                                        SearchCategory.Albums, SearchCategory.Genres -> it.album == name
                                        SearchCategory.Podcasts -> false
                                    }
                                }?.let(onPlaySong)
                            },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(model = art, contentDescription = null, modifier = Modifier.size(56.dp).clip(CircleShape).background(VioletPale))
                            Text(name, color = CharcoalText, style = MaterialTheme.typography.titleLarge)
                        }
                    }
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
    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(model = art, contentDescription = null, modifier = Modifier.size(56.dp).clip(CircleShape).background(VioletPale))
            Column(Modifier.fillMaxWidth(0.68f)) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, color = OliveAccent)
            }
            trailing()
        }
    }
}
