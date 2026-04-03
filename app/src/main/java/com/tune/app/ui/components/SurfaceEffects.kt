package com.tune.app.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.OliveAccent

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    shadowElevation: Dp = 8.dp,
    shadowColor: Color = Color(0x2A4A3480),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.72f), shape)
            .border(1.dp, Color.White.copy(alpha = 0.90f), shape)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            18f, 18f, Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                    .background(Color.White.copy(alpha = 0.10f), shape)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun ClaySurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    baseColor: Color,
    brush: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundModifier = if (brush != null) {
        Modifier.background(brush = brush, shape = shape)
    } else {
        Modifier.background(baseColor)
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color(0x284A3480),
                spotColor = Color(0x384A3480)
            )
            .clip(shape)
            .then(backgroundModifier)
            .border(2.dp, Color.White.copy(alpha = 0.30f), shape),
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
    brush: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ClaySurface(
        modifier = modifier.clickable(onClick = onClick),
        shape = shape,
        baseColor = baseColor,
        brush = brush,
        content = content
    )
}
