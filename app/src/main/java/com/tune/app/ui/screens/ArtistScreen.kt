package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.DeepTeal
import com.tune.app.ui.theme.SeaGreen

@Composable
fun ArtistScreen() {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Brush.linearGradient(listOf(DeepTeal, SeaGreen))),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(0.3f))
                    .height(120.dp)
                    .fillMaxWidth(0.33f)
            )
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Artist Name", style = MaterialTheme.typography.headlineLarge)
            Text("Overview • Albums • Top Tracks", style = MaterialTheme.typography.bodyLarge)
            repeat(5) {
                Box(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surface))
            }
        }
    }
}
