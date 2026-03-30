package com.tune.app.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border

fun Modifier.glassSurface(
    shape: Shape,
    alpha: Float = 0.18f,
    borderAlpha: Float = 0.8f,
    shadowAlpha: Float = 0.16f,
    elevation: Dp = 24.dp
): Modifier = this
    .shadow(elevation = elevation, shape = shape, ambientColor = Color.Black.copy(alpha = shadowAlpha), spotColor = Color.Black.copy(alpha = shadowAlpha))
    .clip(shape)
    .background(Color.White.copy(alpha = alpha), shape)
    .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)

fun Modifier.claySurface(shape: Shape, baseColor: Color): Modifier = this
    .shadow(elevation = 14.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.18f), spotColor = Color.Black.copy(alpha = 0.18f))
    .clip(shape)
    .background(baseColor)
    .drawWithContent {
        drawContent()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.34f), Color.Transparent),
                center = Offset(size.width * 0.30f, size.height * 0.28f),
                radius = size.minDimension * 0.70f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.24f)),
                center = Offset(size.width * 0.76f, size.height * 0.78f),
                radius = size.minDimension * 0.78f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f)),
                center = Offset(size.width * 0.55f, size.height * 0.55f),
                radius = size.minDimension * 0.95f
            )
        )
    }
