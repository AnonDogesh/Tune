package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.DeepTeal
import kotlinx.coroutines.delay

@Composable
fun ScanProgressScreen(onStop: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0.65f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(1200)
            progress = (progress + 0.02f).coerceAtMost(1f)
        }
    }

    Column(
        Modifier.fillMaxSize().background(DeepTeal).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Library Scan", style = MaterialTheme.typography.headlineLarge, color = Color.White)

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.height(420.dp),
                strokeWidth = 18.dp,
                color = Color(0xFFE76F51),
                trackColor = Color(0xFF3A5F70)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(progress * 100).toInt()}%", color = Color(0xFFE9C46A), style = MaterialTheme.typography.headlineLarge)
                Text("PROGRESS", color = Color(0xFFA7B7C7), style = MaterialTheme.typography.titleLarge)
            }
        }

        Text("Scanning Folders", color = Color.White, style = MaterialTheme.typography.headlineLarge)

        Box(Modifier.fillMaxWidth().background(Color(0xFF305364), RoundedCornerShape(16.dp)).padding(16.dp)) {
            Text("Scanning:  /storage/emulated/0/Music/Favorites/NewRelease_2024...", color = Color.White)
        }

        Text("1,248 files found so far", color = Color(0xFFA7B7C7))

        Box(
            Modifier.fillMaxWidth().background(Color(0xFFF4A261), RoundedCornerShape(20.dp)).padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Stop Scan", color = Color.White, style = MaterialTheme.typography.headlineLarge)
        }

        androidx.compose.material3.IconButton(onClick = onStop) {
            Icon(Icons.Default.StopCircle, null, tint = Color(0xFFF4A261))
        }
    }
}
