package com.sangat.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SangatColors {
    val Background        = Color(0xFF07080F)
    val Foreground        = Color(0xFFF2F4FA)
    val Muted             = Color(0xFF1A1C26)
    val MutedForeground   = Color(0xFF8A8FA3)
    val Border            = Color(0x33A0A8C8)
    val CardBgTop         = Color(0x8C2A2E44)
    val CardBgBottom      = Color(0x59101520)
    val CardBg            = Color(0x66161A2A)
    val NeonCyan          = Color(0xFF5BE9F2)
    val Violet            = Color(0xFFB46BFF)
    val Yellow            = Color(0xFFFACC15)
    val PrimaryForeground = Color(0xFF0B0D18)
}

val SangatGradient = Brush.linearGradient(
    listOf(SangatColors.NeonCyan, SangatColors.Violet)
)

val SangatGradientAccent = Brush.linearGradient(
    listOf(SangatColors.Violet, SangatColors.NeonCyan)
)

private val DarkColors = darkColorScheme(
    background   = SangatColors.Background,
    surface      = SangatColors.Background,
    onBackground = SangatColors.Foreground,
    onSurface    = SangatColors.Foreground,
    primary      = SangatColors.NeonCyan,
    onPrimary    = SangatColors.PrimaryForeground,
    secondary    = SangatColors.Violet,
    onSecondary  = Color.White,
    outline      = SangatColors.Border,
)

@Composable
fun SangatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography  = SangatTypography,
        content     = content
    )
}
