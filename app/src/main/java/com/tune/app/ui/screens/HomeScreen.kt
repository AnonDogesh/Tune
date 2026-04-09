package com.tune.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OliveMist
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletMist
import com.tune.app.ui.theme.VioletPale
import com.tune.app.ui.theme.VioletAccent

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
    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
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

    Box(Modifier.fillMaxSize().background(OffWhiteBackground)) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(VioletPale.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 180.dp, y = 200.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(OlivePale.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Library",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = CharcoalText
                    )
                    Text(
                        dailyQuote,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = OliveAccent
                    )
                }
            }

            if (!granted) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier,
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        GlassBox(shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Audio permission required", style = MaterialTheme.typography.titleLarge, color = CharcoalText)
                                Text("Allow audio access to scan and play your offline songs.", color = MutedGreyText)
                                Button(onClick = { launcher.launch(permission) }) { Text("Grant permission") }
                            }
                        }
                    }
                }
            }

            if (recentSongs.isNotEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recent", style = MaterialTheme.typography.titleLarge, color = CharcoalText)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(OliveMist)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("${recentSongs.size}", color = OliveAccent)
                        }
                    }
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 8.dp)) {
                        items(recentSongs.take(10)) { song ->
                            val tileShape = RoundedCornerShape(28.dp)
                            Card(
                                modifier = Modifier
                                    .size(width = 184.dp, height = 228.dp)
                                    .clickable { vm.playSongFromLibrary(song); onNowPlaying() },
                                shape = tileShape,
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                GlassBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(228.dp)
                                        .border(
                                            width = if (currentSong?.id == song.id) 2.dp else 0.dp,
                                            color = if (currentSong?.id == song.id) VioletAccent else Color.Transparent,
                                            shape = tileShape
                                        ),
                                    shape = tileShape,
                                    contentPadding = PaddingValues(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        AsyncImage(
                                            model = song.albumArtUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.22f), CircleShape)
                                                .border(1.dp, Color.White.copy(alpha = 0.65f), CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Text(
                                            song.title,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            color = CharcoalText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            song.artist,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OliveAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Text("All Tracks", style = MaterialTheme.typography.titleLarge, color = CharcoalText) }

            if (songs.isEmpty()) {
                item { Text("No songs found yet. Grant permission and refresh.", color = MutedGreyText) }
            } else {
                items(songs) { song ->
                    var showRemovePopup by remember(song.id) { mutableStateOf(false) }
                    var showConfirmRemove by remember(song.id) { mutableStateOf(false) }
                    val rowShape = RoundedCornerShape(28.dp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.playSongFromLibrary(song); onNowPlaying() },
                        shape = rowShape,
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        GlassBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (currentSong?.id == song.id) 2.dp else 0.dp,
                                    color = if (currentSong?.id == song.id) VioletAccent else Color.Transparent,
                                    shape = rowShape
                                ),
                            shape = rowShape,
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val artworkModel = song.albumArtUri.takeIf { it.isNotBlank() }
                                if (artworkModel == null) {
                                    ClayArtworkPlaceholder()
                                } else {
                                    AsyncImage(
                                        model = artworkModel,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = CharcoalText)
                                    Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = OliveAccent)
                                }
                                Text(song.duration, style = MaterialTheme.typography.labelLarge, color = MutedGreyText)
                                Box {
                                    IconButton(onClick = { showRemovePopup = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = MutedGreyText)
                                    }
                                    DropdownMenu(
                                        expanded = showRemovePopup,
                                        onDismissRequest = { showRemovePopup = false },
                                        shape = RoundedCornerShape(16.dp),
                                        containerColor = OffWhiteBackground.copy(alpha = 0.98f)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Remove from app library", color = CharcoalText) },
                                            onClick = {
                                                showRemovePopup = false
                                                showConfirmRemove = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showConfirmRemove) {
                        Dialog(onDismissRequest = { showConfirmRemove = false }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(OffWhiteBackground.copy(alpha = 0.97f))
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Remove song?", color = CharcoalText, style = MaterialTheme.typography.titleLarge)
                                    Text(
                                        "This song will be hidden from your app library.",
                                        color = MutedGreyText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(onClick = { showConfirmRemove = false }) {
                                            Text("Cancel")
                                        }
                                        Button(
                                            onClick = {
                                                vm.removeSongFromLibrary(song.id)
                                                showConfirmRemove = false
                                            },
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("Remove")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ClayArtworkPlaceholder(
    size: Dp = 56.dp
) {
    ClaySurface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        baseColor = VioletMist
    ) {}
}
