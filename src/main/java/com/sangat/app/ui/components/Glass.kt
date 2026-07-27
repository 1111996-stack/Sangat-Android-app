package com.sangat.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sangat.app.ui.theme.SangatColors

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    corner: Dp = 24.dp,
    strong: Boolean = false,
    content: @Composable () -> Unit,
) {
    val bg = Brush.linearGradient(
        if (strong)
            listOf(Color(0xB32E3350), Color(0x80161A2A))
        else
            listOf(Color(0x8C232744), Color(0x59101422))
    )
    val borderColor = if (strong) SangatColors.NeonCyan.copy(alpha = 0.2f)
                      else Color.White.copy(alpha = 0.08f)
    Box(
        modifier
            .clip(RoundedCornerShape(corner))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(corner))
    ) { content() }
}

@Composable
fun AmbientBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(SangatColors.Background)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(SangatColors.Violet.copy(alpha = 0.30f), Color.Transparent),
                    radius = 700f
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(SangatColors.NeonCyan.copy(alpha = 0.22f), Color.Transparent),
                    radius = 800f
                )
            )
        )
        content()
    }
}
