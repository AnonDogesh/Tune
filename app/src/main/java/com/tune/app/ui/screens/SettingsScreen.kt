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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tune.app.ui.components.ClaySurface
import com.tune.app.ui.components.GlassBox
import com.tune.app.ui.theme.CharcoalText
import com.tune.app.ui.theme.GlassWhite
import com.tune.app.ui.theme.MutedGreyText
import com.tune.app.ui.theme.OffWhiteBackground
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.OliveDark
import com.tune.app.ui.theme.OliveLight
import com.tune.app.ui.theme.OliveMist
import com.tune.app.ui.theme.OlivePale
import com.tune.app.ui.theme.VioletAccent
import com.tune.app.ui.theme.VioletDark
import com.tune.app.ui.theme.VioletLight
import com.tune.app.ui.theme.VioletPale

@Composable
fun SettingsScreen(
    onEqualizer: () -> Unit,
    onScanMusic: () -> Unit,
    onScanProgress: () -> Unit
) {
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
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            "App Settings and Configuration",
            style = MaterialTheme.typography.headlineLarge,
            color = CharcoalText,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        SectionTitle("AUDIO")
        SettingRow("Equalizer", icon = { Icon(Icons.Default.Equalizer, null, tint = Color.White) }, onClick = onEqualizer)

        SectionTitle("LIBRARY")
        SettingRow("Scan Music Folder", icon = { Icon(Icons.Default.Folder, null, tint = Color.White) }, onClick = onScanMusic)
        SettingRow("Exclude Folders", subtitle = "Configure folders to ignore", icon = { Icon(Icons.Default.FolderOff, null, tint = Color.White) }, onClick = {})
        SettingRow("Manage Storage", subtitle = "12.4 GB", icon = { Icon(Icons.Default.Storage, null, tint = Color.White) }, onClick = onScanProgress)

        SectionTitle("PERSONALIZATION")
        SettingRow("Sleep Timer", subtitle = "Off", icon = { Icon(Icons.Default.Bedtime, null, tint = Color.White) }, onClick = {})

        GlassBox(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(32.dp),
            contentPadding = PaddingValues(20.dp)
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ABOUT", color = OliveAccent, style = MaterialTheme.typography.labelSmall)
                ClaySurface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    baseColor = OliveAccent,
                    brush = Brush.linearGradient(listOf(OliveLight, OliveDark))
                ) {
                    Text("♪", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
                Box(Modifier.background(GlassWhite, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("v1.2.4", color = VioletAccent, style = MaterialTheme.typography.titleMedium)
                }
                Text("Designed and developed with passion\nfor high-fidelity audio.", color = MutedGreyText, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = OliveAccent,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String? = null,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val brush = when (title) {
        "Equalizer" -> Brush.linearGradient(listOf(VioletLight, VioletDark))
        "Scan Music Folder" -> Brush.linearGradient(listOf(OliveLight, OliveDark))
        "Exclude Folders" -> Brush.linearGradient(listOf(Color(0xFFB87333), Color(0xFF8B5020)))
        "Manage Storage" -> Brush.linearGradient(listOf(Color(0xFF7A9E9F), Color(0xFF4A7172)))
        else -> Brush.linearGradient(listOf(VioletLight, VioletDark))
    }
    Box(Modifier.padding(bottom = 10.dp)) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ClaySurface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(14.dp), baseColor = OliveAccent, brush = brush) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { icon() }
                }
                Text(title, color = CharcoalText, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                if (subtitle != null) Text(subtitle, color = MutedGreyText, style = MaterialTheme.typography.bodyLarge)
                Box(Modifier.size(28.dp).background(OliveMist, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ChevronRight, null, tint = OliveAccent)
                }
            }
        }
    }
}
