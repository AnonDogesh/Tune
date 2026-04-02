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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tune.app.ui.theme.GlassWhite
import com.tune.app.ui.theme.OliveAccent
import com.tune.app.ui.theme.ShadowSoft

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = shape, spotColor = ShadowSoft)
            .background(GlassWhite, shape)
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
                .background(GlassWhite)
                .border(0.5.dp, Color.White.copy(alpha = 0.85f), shape)
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
                ambientColor = ShadowSoft,
                spotColor = ShadowSoft
            )
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.White.copy(alpha = 0.12f),
                spotColor = Color.White.copy(alpha = 0.12f)
            )
            .clip(shape)
            .then(backgroundModifier)
            .border(2.dp, Color.White.copy(alpha = 0.35f), shape),
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
