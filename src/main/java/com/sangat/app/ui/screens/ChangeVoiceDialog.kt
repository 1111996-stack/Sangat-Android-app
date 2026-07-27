package com.sangat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sangat.app.ui.components.GlassSurface
import com.sangat.app.ui.theme.SangatColors
import com.sangat.app.ui.theme.SangatGradient

@Composable
fun ChangeVoiceDialog(
    currentMode: VoiceMode,
    onClose: () -> Unit,
    onConfirmApp: () -> Unit,
    onConfirmOwn: () -> Unit,
) {
    var warn by remember { mutableStateOf<VoiceMode?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassSurface(corner = 32.dp, strong = true, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp).padding(bottom = 16.dp)) {
                Box(
                    Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                        .size(40.dp, 4.dp).clip(RoundedCornerShape(2.dp)).background(Color.White.copy(0.2f))
                )
                if (warn == null) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f)) {
                            Text("Change Voice", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
                            Text("Switch the voice that plays on battery events.",
                                fontSize = 13.sp, color = SangatColors.MutedForeground, modifier = Modifier.padding(top = 4.dp))
                        }
                        GlassSurface(corner = 999.dp, modifier = Modifier.size(36.dp).clickable { onClose() }) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, null, tint = SangatColors.Foreground, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    VoiceChoiceRow(active = currentMode == VoiceMode.APP, icon = Icons.Default.GraphicEq,
                        title = "App Voice", desc = "Default funny lines", accent = SangatColors.NeonCyan,
                        onClick = { warn = VoiceMode.APP })
                    Spacer(Modifier.height(12.dp))
                    VoiceChoiceRow(active = currentMode == VoiceMode.OWN, icon = Icons.Default.Mic,
                        title = "Add Own Voice", desc = "Re-record 3 alerts", accent = SangatColors.Violet,
                        onClick = { warn = VoiceMode.OWN })
                } else {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SangatColors.Yellow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Warning, null, tint = SangatColors.Yellow)
                    }
                    Text(
                        if (warn == VoiceMode.APP) "Switch to App Voice?" else "Re-record Own Voice?",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        if (warn == VoiceMode.APP)
                            "This will replace your custom voice with the default app voice. Your old recordings will be deleted."
                        else "Recording a new voice will replace your existing custom voice.",
                        fontSize = 13.sp, color = SangatColors.MutedForeground, modifier = Modifier.padding(top = 8.dp)
                    )
                    Row(Modifier.padding(top = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassSurface(corner = 20.dp, modifier = Modifier.weight(1f).clickable { warn = null }) {
                            Box(Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                                Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.Foreground)
                            }
                        }
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(SangatGradient)
                                .clickable { if (warn == VoiceMode.APP) onConfirmApp() else onConfirmOwn() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("OK, continue", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.PrimaryForeground)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceChoiceRow(
    active: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, desc: String, accent: Color, onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.04f)).clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.Foreground)
            Text(desc,  fontSize = 11.sp, color = SangatColors.MutedForeground)
        }
        if (active) {
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(accent.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = accent)
            }
        }
    }
}
