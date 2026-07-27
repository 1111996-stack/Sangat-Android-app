package com.sangat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sangat.app.ui.components.AmbientBackground
import com.sangat.app.ui.components.GlassSurface
import com.sangat.app.ui.theme.SangatColors

enum class VoiceMode { OWN, APP }

@Composable
fun OptionScreen(onPick: (VoiceMode) -> Unit) {
    AmbientBackground {
        Column(
            Modifier.fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 32.dp)
        ) {
            Text("STEP 02", color = SangatColors.MutedForeground, fontSize = 10.sp, letterSpacing = 4.sp)
            Text(
                "Choose your vibe",
                fontSize = 30.sp, fontWeight = FontWeight.Bold,
                color = SangatColors.Foreground, modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Pick how Sangat should speak when your battery wakes up.",
                color = SangatColors.MutedForeground, fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(32.dp))
            OptionCard(
                title       = "Add Own Voice",
                desc        = "Record 3 alerts in your own voice. Personal and fun.",
                cta         = "Start recording →",
                accentColor = SangatColors.Violet,
                iconGradient= Brush.linearGradient(listOf(SangatColors.Violet, SangatColors.NeonCyan)),
                icon        = Icons.Default.Mic,
                onClick     = { onPick(VoiceMode.OWN) },
            )
            Spacer(Modifier.height(16.dp))
            OptionCard(
                title       = "App Voice",
                desc        = "Pre-recorded funny lines in Hindi & Urdu. Ready instantly.",
                cta         = "Use default voice →",
                accentColor = SangatColors.NeonCyan,
                iconGradient= Brush.linearGradient(listOf(SangatColors.NeonCyan, SangatColors.Violet)),
                icon        = Icons.Default.GraphicEq,
                onClick     = { onPick(VoiceMode.APP) },
            )
            Spacer(Modifier.weight(1f))
            Text(
                "You can switch any time",
                color = SangatColors.MutedForeground, fontSize = 10.sp, letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OptionCard(
    title: String, desc: String, cta: String,
    accentColor: androidx.compose.ui.graphics.Color,
    iconGradient: Brush,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    GlassSurface(corner = 28.dp, modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(iconGradient),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = SangatColors.PrimaryForeground) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
                Text(desc, fontSize = 12.sp, color = SangatColors.MutedForeground, modifier = Modifier.padding(top = 4.dp))
                Text(cta,  fontSize = 12.sp, color = accentColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}
