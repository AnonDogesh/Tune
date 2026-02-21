package com.tune.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tune.app.ui.components.BouncyIconButton
import com.tune.app.ui.theme.CoralRed
import com.tune.app.ui.theme.WarmOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen() {
    var progress by remember { mutableFloatStateOf(0.35f) }
    val waves = rememberInfiniteTransition(label = "waves")
    val waveScale by waves.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "waveScale"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary.copy(0.2f))
        )

        Text("Late Night Drive", style = MaterialTheme.typography.titleLarge)
        Text("Tune Artist", style = MaterialTheme.typography.bodyLarge)

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(CoralRed)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(WarmOrange, CoralRed)))
                )
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            BouncyIconButton(shape = RoundedCornerShape(100), onClick = {}) { Icon(Icons.Default.SkipPrevious, null) }
            BouncyIconButton(shape = CircleShape, onClick = {}) { Icon(Icons.Default.Pause, null) }
            BouncyIconButton(shape = RoundedCornerShape(100), onClick = {}) { Icon(Icons.Default.SkipNext, null) }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(18) {
                Box(
                    Modifier
                        .size(width = 6.dp, height = 20.dp)
                        .scale(scaleX = 1f, scaleY = waveScale)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
