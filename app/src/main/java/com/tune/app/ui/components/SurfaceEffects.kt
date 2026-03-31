package com.tune.app.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.OliveAccent

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White.copy(alpha = 0.08f), shape)
            .clip(shape)
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            25f,
                            25f,
                            android.graphics.Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                }
                .background(Color.White.copy(alpha = 0.15f))
                .border(0.5.dp, Color.White.copy(alpha = 0.45f), shape)
        )
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun ClaySurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    baseColor: Color,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.16f)
            )
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.White.copy(alpha = 0.12f),
                spotColor = Color.White.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(baseColor)
            .border(2.dp, Color.White.copy(alpha = 0.2f), shape),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    baseColor: Color = OliveAccent,
    content: @Composable BoxScope.() -> Unit
) {
    ClaySurface(
        modifier = modifier.clickable(onClick = onClick),
        shape = shape,
        baseColor = baseColor,
        content = content
    )
}
