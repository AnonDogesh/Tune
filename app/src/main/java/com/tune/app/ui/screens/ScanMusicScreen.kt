package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.DeepTeal

@Composable
fun ScanMusicScreen(onBack: () -> Unit, onStartScan: () -> Unit) {
    val folders = remember {
        mutableStateListOf(
            "/storage/emulated/0/Music" to "152 files • 1.2 GB",
            "/storage/emulated/0/Downloads" to "24 files • 180 MB",
            "/SDCard/Hi-Res/Jazz" to "89 files • 4.5 GB"
        )
    }

    Column(Modifier.fillMaxSize().background(DeepTeal).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, tint = Color.White) }
            Text("Scan Music", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFE9C46A))
        }

        Text("FOLDERS TO SCAN", color = Color(0xFF2A9D8F), style = MaterialTheme.typography.titleLarge)

        folders.forEachIndexed { index, (path, stats) ->
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF305364), RoundedCornerShape(22.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(Icons.Default.Folder, null, tint = Color(0xFF2A9D8F))
                Column(Modifier.weight(1f)) {
                    Text(path, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(stats, color = Color(0xFFA7B7C7), style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = { folders.removeAt(index) }) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFE76F51))
                }
            }
        }

        Text("Add more directories to expand your library", color = Color(0xFFA7B7C7), modifier = Modifier.align(Alignment.CenterHorizontally))

        Row(
            Modifier.fillMaxWidth().background(Color(0xFF2A9D8F), RoundedCornerShape(20.dp)).padding(18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FolderOpen, null, tint = Color.White)
            Text("  Add Folder", color = Color.White, style = MaterialTheme.typography.headlineLarge)
        }

        Row(
            Modifier.fillMaxWidth().background(Color(0xFFF4A261), RoundedCornerShape(20.dp)).clickable { onStartScan() }.padding(18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Sync, null, tint = Color.White)
            Text("  Start Scan", color = Color.White, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(start = 6.dp))
        }
    }
}
