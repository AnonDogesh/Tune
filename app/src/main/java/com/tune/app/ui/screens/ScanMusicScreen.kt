package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OliveDark
import com.tune.app.ui.theme.OliveLight
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletDark
import com.tune.app.ui.theme.VioletLight
import com.tune.app.ui.theme.VioletPale

@Composable
fun ScanMusicScreen(onBack: () -> Unit, onStartScan: () -> Unit) {
    val folders = remember {
        mutableStateListOf(
            "/storage/emulated/0/Music" to "152 files • 1.2 GB",
            "/storage/emulated/0/Downloads" to "24 files • 180 MB",
            "/SDCard/Hi-Res/Jazz" to "89 files • 4.5 GB"
        )
    }

    Box(Modifier.fillMaxSize().background(OffWhiteBackground)) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(Brush.radialGradient(listOf(VioletPale.copy(alpha = 0.5f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 180.dp, y = 200.dp)
                .background(Brush.radialGradient(listOf(OlivePale.copy(alpha = 0.4f), Color.Transparent)), CircleShape)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBackIosNew, null, tint = VioletAccent)
                        }
                    }
                    Column {
                        Text("Scan Music", color = CharcoalText, style = MaterialTheme.typography.headlineLarge)
                        Text("Choose folders to include in library scan", color = MutedGreyText)
                    }
                }
            }

            item {
                Text("FOLDERS TO SCAN", color = OliveAccent, style = MaterialTheme.typography.labelSmall)
            }

            items(folders) { (path, stats) ->
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClaySurface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            baseColor = OliveAccent,
                            brush = Brush.linearGradient(listOf(OliveLight, OliveDark))
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Folder, null, tint = Color.White)
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text(path.substringAfterLast('/'), color = CharcoalText, style = MaterialTheme.typography.titleMedium)
                            Text(path, color = MutedGreyText, style = MaterialTheme.typography.bodySmall)
                            Text(stats, color = OliveAccent, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            item {
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.FolderOpen, null, tint = OliveAccent)
                        Text("Add Folder", color = CharcoalText, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartScan() },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sync, null, tint = VioletAccent)
                        Text("  Start Scan", color = VioletAccent, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
