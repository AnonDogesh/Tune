package com.tune.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun BouncyIconButton(
    shape: Shape,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1.05f,
        animationSpec = spring(dampingRatio = 0.45f),
        label = "bouncy"
    )
    Surface(
        modifier = Modifier
            .scale(scale)
            .clickable {
                pressed = true
                onClick()
                pressed = false
            },
        shape = shape,
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(Modifier.padding(14.dp)) {
            content()
        }
    }
}
