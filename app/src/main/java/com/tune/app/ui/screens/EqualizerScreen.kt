package com.tune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.state.TuneViewModel
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletPale

@Composable
fun EqualizerScreen(vm: TuneViewModel, onBack: () -> Unit) {
    val enabled by vm.equalizerEnabled.collectAsStateWithLifecycle()
    val bands by vm.equalizerBands.collectAsStateWithLifecycle()
    val presets by vm.equalizerPresets.collectAsStateWithLifecycle()
    val selectedPreset by vm.equalizerSelectedPreset.collectAsStateWithLifecycle()
    val levelRange by vm.equalizerLevelRange.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.refreshEqualizerState() }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassBox(modifier = Modifier.size(44.dp), shape = CircleShape) {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, tint = VioletAccent) }
                    }
                    Icon(Icons.Default.Equalizer, null, tint = VioletAccent, modifier = Modifier.padding(start = 12.dp))
                    Text(
                        "  Equalizer",
                        style = MaterialTheme.typography.headlineLarge,
                        color = CharcoalText,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = enabled, onCheckedChange = vm::setEqualizerEnabled)
                }
            }

            if (presets.isNotEmpty()) {
                item {
                    Text("Presets", color = OliveAccent, style = MaterialTheme.typography.labelSmall)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(presets) { idx, preset ->
                            val active = idx == selectedPreset
                            ClaySurface(
                                modifier = Modifier
                                    .height(42.dp)
                                    .clickable { vm.applyEqualizerPreset(idx) },
                                shape = RoundedCornerShape(14.dp),
                                baseColor = if (active) VioletAccent else Color.White,
                                brush = if (active) Brush.linearGradient(listOf(VioletPale, VioletAccent)) else null
                            ) {
                                Text(
                                    preset,
                                    modifier = Modifier
                                        .padding(horizontal = 14.dp)
                                        .align(Alignment.Center),
                                    color = if (active) Color.White else CharcoalText
                                )
                            }
                        }
                    }
                }
            }

            if (bands.isEmpty()) {
                item {
                    GlassBox(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        glassAlpha = 0.88f
                    ) {
                        Text("Start playback to enable the device equalizer.", color = MutedGreyText)
                    }
                }
            } else {
                item {
                    GlassBox(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                        glassAlpha = 0.88f
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                bands.forEachIndexed { idx, band ->
                                    val normalized = ((band.level - levelRange.first).toFloat() /
                                        (levelRange.second - levelRange.first).toFloat()).coerceIn(0f, 1f)
                                    Text("${band.label}  ${band.level / 100f}dB", color = OliveAccent, style = MaterialTheme.typography.labelSmall)
                                    Slider(
                                        value = normalized,
                                        onValueChange = { vm.setEqualizerBandLevel(idx, it) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
