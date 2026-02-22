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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.DeepTeal

@Composable
fun SettingsScreen(
    onEqualizer: () -> Unit,
    onScanMusic: () -> Unit,
    onScanProgress: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(DeepTeal)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            "App Settings and Configuration",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        SectionTitle("AUDIO")
        SettingRow("Equalizer", icon = { Icon(Icons.Default.Equalizer, null, tint = Color(0xFF2A9D8F)) }, onClick = onEqualizer)

        SectionTitle("LIBRARY")
        SettingRow("Scan Music Folder", icon = { Icon(Icons.Default.Folder, null, tint = Color(0xFF2A9D8F)) }, onClick = onScanMusic)
        SettingRow("Exclude Folders", subtitle = "Configure folders to ignore", icon = { Icon(Icons.Default.FolderOff, null, tint = Color(0xFF2A9D8F)) }, onClick = {})
        SettingRow("Manage Storage", subtitle = "12.4 GB", icon = { Icon(Icons.Default.Storage, null, tint = Color(0xFF2A9D8F)) }, onClick = onScanProgress)

        SectionTitle("PERSONALIZATION")
        SettingRow("Sleep Timer", subtitle = "Off", icon = { Icon(Icons.Default.Bedtime, null, tint = Color(0xFF2A9D8F)) }, onClick = {})

        Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ABOUT", color = Color(0xFFE9C46A), style = MaterialTheme.typography.titleMedium)
            Text("v1.2.4", color = Color(0xFFA7B7C7), style = MaterialTheme.typography.titleLarge)
            Text("Designed and developed with passion\nfor high-fidelity audio.", color = Color(0xFF7F94A5), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = Color(0xFFE9C46A),
        style = MaterialTheme.typography.titleLarge,
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
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF305364), RoundedCornerShape(0.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icon()
        Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        if (subtitle != null) Text(subtitle, color = Color(0xFFA7B7C7), style = MaterialTheme.typography.bodyLarge)
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFA7B7C7))
    }
}
