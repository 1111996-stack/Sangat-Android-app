package com.sangat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sangat.app.ui.components.AmbientBackground
import com.sangat.app.ui.components.GlassSurface
import com.sangat.app.ui.components.SangatLogo
import com.sangat.app.ui.theme.SangatColors
import com.sangat.app.ui.theme.SangatGradient
import com.sangat.app.ui.theme.SangatGradientAccent

@Composable
fun MainScreen(
    mode: VoiceMode,
    serviceOn: Boolean,
    onToggleService: (Boolean) -> Unit,
    onChangeVoice: () -> Unit,
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(100) }
    var batteryHealth by remember { mutableStateOf("GOOD") }

    fun mapHealth(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
        BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER VOLTAGE"
        BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
        else -> "HEALTHY"
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        
        // Get initial state
        val initialIntent = context.registerReceiver(null, filter)
        initialIntent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryLevel = (level * 100 / scale.toFloat()).toInt()
            }
            batteryHealth = mapHealth(it.getIntExtra(BatteryManager.EXTRA_HEALTH, -1))
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (level != -1 && scale != -1) {
                        batteryLevel = (level * 100 / scale.toFloat()).toInt()
                    }
                    batteryHealth = mapHealth(it.getIntExtra(BatteryManager.EXTRA_HEALTH, -1))
                }
            }
        }
        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    AmbientBackground {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 32.dp)
        ) {
            // header
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 56.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.sangat.app.R.drawable.small_logo),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Sangat", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
                    if (serviceOn) {
                        Text(
                            "LISTENING · $batteryLevel% · $batteryHealth",
                            fontSize = 10.sp, letterSpacing = 2.sp, color = SangatColors.MutedForeground
                        )
                    } else {
                        Text(
                            "BATTERY $batteryLevel% · $batteryHealth",
                            fontSize = 10.sp, letterSpacing = 2.sp, color = SangatColors.MutedForeground
                        )
                    }
                }
                GlassSurface(corner = 999.dp, modifier = Modifier.clickable { onChangeVoice() }) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SwapHoriz, null, tint = SangatColors.Foreground, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Change", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SangatColors.Foreground)
                    }
                }
            }

            Column(
                Modifier.padding(horizontal = 20.dp).padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero toggle card
                GlassSurface(corner = 32.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(8.dp).clip(RoundedCornerShape(50))
                                    .background(if (serviceOn) SangatColors.NeonCyan else SangatColors.MutedForeground)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (serviceOn) "SERVICE IS ACTIVE" else "INACTIVE",
                                fontSize = 10.sp, letterSpacing = 2.sp,
                                color = SangatColors.MutedForeground, fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Column(Modifier.weight(1f)) {
                                Text("Battery Voice", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
                                Text(
                                    if (serviceOn) "Will speak on plug-in, unplug & full."
                                    else "Tap to start listening.",
                                    fontSize = 13.sp, color = SangatColors.MutedForeground,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Switch(
                                checked = serviceOn, onCheckedChange = onToggleService,
                                modifier = Modifier.scale(1.2f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor  = SangatColors.PrimaryForeground,
                                    checkedTrackColor  = SangatColors.NeonCyan,
                                    uncheckedTrackColor= SangatColors.Muted,
                                )
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.04f)).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val accent = if (mode == VoiceMode.OWN) SangatColors.Violet else SangatColors.NeonCyan
                            Box(
                                Modifier.size(40.dp).clip(RoundedCornerShape(14.dp))
                                    .background(accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center) {
                                Icon(
                                    if (mode == VoiceMode.OWN) Icons.Default.Mic else Icons.Default.GraphicEq,
                                    null, tint = accent
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (mode == VoiceMode.OWN) "Own Voice" else "App Voice",
                                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.Foreground
                                )
                                Text(
                                    if (mode == VoiceMode.OWN) "Your 3 custom recordings" else "Funny default lines",
                                    fontSize = 11.sp, color = SangatColors.MutedForeground
                                )
                            }
                            Box(
                                Modifier.clip(RoundedCornerShape(999.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable { onChangeVoice() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Change Voice", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.Foreground)
                            }
                        }
                    }
                }

                // Events card
                GlassSurface(corner = 24.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(44.dp).clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.PowerSettingsNew, null, tint = SangatColors.Foreground) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("What plays", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = SangatColors.Foreground)
                                Text("Triggered automatically in the background", fontSize = 11.sp, color = SangatColors.MutedForeground)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        EventRow(Icons.Default.BatteryChargingFull, "CHARGING START",
                            if (mode == VoiceMode.OWN) "Your recorded greeting" else "Yahoo! Charging shuru! Ab party shuru!",
                            SangatColors.NeonCyan, SangatColors.NeonCyan.copy(alpha = 0.10f))
                        Spacer(Modifier.height(8.dp))
                        EventRow(Icons.Default.BatteryAlert, "CHARGING STOP",
                            if (mode == VoiceMode.OWN) "Your recorded farewell" else "Oho! Charger nikal diya? Meri jaan nikal di.",
                            SangatColors.Violet, SangatColors.Violet.copy(alpha = 0.10f))
                        Spacer(Modifier.height(8.dp))
                        EventRow(Icons.Default.BatteryFull, "BATTERY FULL",
                            if (mode == VoiceMode.OWN) "Your recorded victory line" else "Battery full! Ab main 100% jawaan hun.",
                            SangatColors.NeonCyan,
                            Brush.linearGradient(listOf(SangatColors.NeonCyan.copy(0.15f), SangatColors.Violet.copy(0.15f))))
                    }
                }

                // Change voice CTA
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(SangatGradientAccent).clickable { onChangeVoice() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, null, tint = SangatColors.PrimaryForeground)
                        Spacer(Modifier.width(8.dp))
                        Text("Change Voice", color = SangatColors.PrimaryForeground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Text(
                    "BATTERY WHISPERER · SINCE 2026",
                    color = SangatColors.MutedForeground, fontSize = 10.sp, letterSpacing = 2.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EventRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, line: String, iconColor: Color, bg: Any) {
    val mod = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).let {
        when (bg) {
            is Brush -> it.background(bg)
            is Color -> it.background(bg)
            else     -> it
        }
    }.padding(12.dp)
    Row(mod, verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp, color = SangatColors.Foreground.copy(0.9f))
            Text("\"$line\"", fontSize = 12.sp, color = SangatColors.MutedForeground, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
