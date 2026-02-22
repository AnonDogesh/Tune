package com.tune.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.DeepTeal

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    vm: TuneViewModel,
    onNowPlaying: () -> Unit,
    onArtist: () -> Unit,
    onAlbum: () -> Unit
) {
    val songs by vm.songs.collectAsStateWithLifecycle()
    val recentSongs by vm.recentSongs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) vm.refreshLibrary()
    }
    val libraryQuotes = remember {
        listOf(
            "For the love of music.",
            "Music is a treat to the ears.",
            "Let every beat tell a story.",
            "Keep calm and press play.",
            "Find your rhythm, every day."
        )
    }
    val dailyQuote = remember { libraryQuotes.random() }

    LaunchedEffect(granted) {
        if (granted) vm.refreshLibrary() else launcher.launch(permission)
    }

    Box(Modifier.fillMaxSize().background(DeepTeal)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Library", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
                    Text(
                        dailyQuote,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            item {
                OutlinedTextField(value = "", onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50), placeholder = { Text("Search your library...") })
            }

            if (!granted) {
                item {
                    Card(shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Audio permission required", style = MaterialTheme.typography.titleLarge)
                            Text("Allow audio access to scan and play your offline songs.")
                            Button(onClick = { launcher.launch(permission) }) { Text("Grant permission") }
                        }
                    }
                }
            }

            if (recentSongs.isNotEmpty()) {
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary); Text("${recentSongs.size}", color = MaterialTheme.colorScheme.primary) } }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 8.dp)) {
                        items(recentSongs.take(10)) { song ->
                            Card(modifier = Modifier.size(width = 184.dp, height = 228.dp), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(Modifier.padding(12.dp).clickable { vm.playSongFromLibrary(song); onNowPlaying() }) {
                                    AsyncImage(model = song.albumArtUri, contentDescription = null, modifier = Modifier.size(160.dp).background(MaterialTheme.colorScheme.primary.copy(0.2f), RoundedCornerShape(22.dp)))
                                    Text(song.title, modifier = Modifier.fillMaxWidth(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(song.artist, modifier = Modifier.fillMaxWidth(), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            item { Text("All Tracks", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary) }

            if (songs.isEmpty()) {
                item { Text("No songs found yet. Grant permission and refresh.", color = MaterialTheme.colorScheme.onPrimary) }
            } else {
                items(songs) { song ->
                    var expanded by remember(song.id) { mutableStateOf(false) }
                    Card(modifier = Modifier.fillMaxWidth().clickable { vm.playSongFromLibrary(song); onNowPlaying() }, shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AsyncImage(model = song.albumArtUri, contentDescription = null, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.secondary.copy(0.25f), CircleShape))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onPrimary)
                                Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                            Text(song.duration, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(text = { Text("Remove from app library") }, onClick = {
                                        vm.removeSongFromLibrary(song.id)
                                        expanded = false
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { songs.firstOrNull()?.let { vm.playSongFromLibrary(it); onNowPlaying() } },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.tertiary
        ) { Icon(Icons.Default.PlayArrow, contentDescription = "play") }
    }
}
