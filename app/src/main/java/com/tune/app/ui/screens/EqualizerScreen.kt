package com.tune.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.DeepTeal

@Composable
fun EqualizerScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    val freqs = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    val values = remember { freqs.map { mutableFloatStateOf(0.5f) } }

    Column(Modifier.fillMaxSize().background(DeepTeal).padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, tint = Color.White) }
            Icon(Icons.Default.Equalizer, null, tint = Color.White)
            Text("  Equalizer", style = MaterialTheme.typography.headlineLarge, color = Color.White, modifier = Modifier.weight(1f))
            Text("MASTER", color = Color(0xFFA7B7C7), style = MaterialTheme.typography.titleMedium)
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }

        Row(
            Modifier.fillMaxWidth().background(Color(0xFFF4A261), RoundedCornerShape(20.dp)).padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Preset: Rock", style = MaterialTheme.typography.headlineLarge, color = DeepTeal)
            Text("⌄", style = MaterialTheme.typography.headlineLarge, color = DeepTeal)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
            values.forEachIndexed { i, slider ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${((slider.floatValue - 0.5f) * 12).toInt()}dB", color = Color(0xFFE9C46A))
                    Slider(
                        value = slider.floatValue,
                        onValueChange = { slider.floatValue = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.height(240.dp).width(36.dp)
                    )
                    Text(freqs[i], color = Color(0xFFA7B7C7))
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF1F3D4B), RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            val p = Path()
            p.moveTo(0f, size.height * 0.55f)
            p.cubicTo(size.width * 0.25f, size.height * 0.2f, size.width * 0.45f, size.height * 0.8f, size.width, size.height * 0.55f)
            drawPath(p, Color(0xFFE9C46A))
            repeat(20) { idx ->
                val x = size.width / 20f * idx
                val barH = size.height * (0.2f + (idx % 5) * 0.12f)
                drawLine(
                    color = if (idx % 3 == 0) Color(0xFFEF7F5E) else Color(0xFF2A9D8F),
                    start = androidx.compose.ui.geometry.Offset(x, size.height - 20f),
                    end = androidx.compose.ui.geometry.Offset(x, size.height - barH),
                    strokeWidth = 8f
                )
            }
        }
    }
}
