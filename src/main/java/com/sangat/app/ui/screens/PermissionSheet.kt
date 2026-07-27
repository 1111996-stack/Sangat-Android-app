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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sangat.app.ui.components.GlassSurface
import com.sangat.app.ui.theme.SangatColors
import com.sangat.app.ui.theme.SangatGradient

private data class Perm(val icon: ImageVector, val name: String, val desc: String)

@Composable
fun PermissionSheet(onAllow: () -> Unit) {
    val perms = listOf(
        Perm(Icons.Default.Mic,            "Microphone",       "To record your own voice alerts"),
        Perm(Icons.Default.Notifications,  "Notifications",    "Show background service status"),
        Perm(Icons.Default.BatteryFull,    "Battery Stats",    "Detect charging & full events"),
        Perm(Icons.Default.VolumeUp,       "Audio Settings",   "Duck media when speaking"),
        Perm(Icons.Default.Shield,         "Foreground Service","Keep working when app is closed"),
    )
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassSurface(corner = 32.dp, strong = true, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp).padding(bottom = 16.dp)) {
                Box(
                    Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                        .size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(0.2f))
                )
                Text("Quick permissions", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
                Text(
                    "Sangat needs these to deliver voice alerts even when the app is closed.",
                    fontSize = 13.sp, color = SangatColors.MutedForeground,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(20.dp))
                perms.forEach { p ->
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.03f)).padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            Modifier.size(40.dp).clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(listOf(
                                        SangatColors.NeonCyan.copy(0.3f),
                                        SangatColors.Violet.copy(0.3f)
                                    ))
                                ),
                            contentAlignment = Alignment.Center
                        ) { Icon(p.icon, null, tint = SangatColors.NeonCyan) }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = SangatColors.Foreground)
                            Text(p.desc, fontSize = 12.sp, color = SangatColors.MutedForeground)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(SangatGradient).clickable { onAllow() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Allow all & continue",
                        color = SangatColors.PrimaryForeground,
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                    )
                }
            }
        }
    }
}
