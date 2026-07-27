package com.sangat.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sangat.app.RecorderHelper
import com.sangat.app.utils.SharedPrefsHelper
import com.sangat.app.ui.components.AmbientBackground
import com.sangat.app.ui.theme.SangatColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecorderWizard(onClose: () -> Unit, onDone: () -> Unit) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val steps     = listOf("Charging Started", "Charging Stopped", "Battery Full", "Battery 15%", "Battery 5%", "Battery Overheat")
    val eventKeys = listOf("charging_start", "charging_stop", "battery_full", "battery_low_15", "battery_low_5", "battery_overheat")

    var step             by remember { mutableStateOf(0) }
    var isRecording      by remember { mutableStateOf(false) }
    val recordedPaths    = remember { mutableStateListOf<String?>(null, null, null, null, null, null) }
    var statusText       by remember { mutableStateOf("Hold mic to record") }
    var timerText        by remember { mutableStateOf("0:00") }

    val recorderHelper = remember { RecorderHelper(context) }

    // Pulsing animation for mic button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "scale"
    )

    // Timer var
    var timerJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Cleanup on leave
    DisposableEffect(Unit) {
        onDispose { recorderHelper.release() }
    }

    AmbientBackground {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(24.dp)) {

            // ── Header ─────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Step ${step+1}/6", fontSize = 14.sp, color = SangatColors.NeonCyan)
                Spacer(Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = (step + 1) / 6f,
                    modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = SangatColors.NeonCyan,
                    trackColor = SangatColors.NeonCyan.copy(alpha = 0.2f)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { recorderHelper.release(); onClose() }) {
                    Icon(Icons.Default.Close, null, tint = SangatColors.MutedForeground)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(steps[step], fontSize = 26.sp, fontWeight = FontWeight.Bold, color = SangatColors.Foreground)
            Text("Record your voice for this event", fontSize = 13.sp, color = SangatColors.MutedForeground)

            Spacer(Modifier.height(60.dp))

            // ── Mic Button ─────────────────────────────────────
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(70.dp))
                        .background(
                            if (isRecording) SangatColors.Violet.copy(alpha = 0.25f)
                            else SangatColors.NeonCyan.copy(alpha = 0.1f)
                        )
                )
                // Inner mic button — hold to record
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(45.dp))
                        .background(
                            if (isRecording) SangatColors.Violet
                            else SangatColors.NeonCyan
                        )
                        .pointerInput(step) {
                            detectTapGestures(
                                onPress = {
                                    // Check permission
                                    val hasPerm = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasPerm) {
                                        statusText = "Mic permission denied!"
                                        return@detectTapGestures
                                    }

                                    // START recording
                                    recorderHelper.startRecording(eventKeys[step])
                                    isRecording = true
                                    statusText = "Recording..."
                                    var sec = 0
                                    timerJob = scope.launch {
                                        while (true) {
                                            delay(1000)
                                            sec++
                                            timerText = "${sec / 60}:${String.format("%02d", sec % 60)}"
                                        }
                                    }

                                    // Wait for release handled by pointerInput logic typically
                                    // In this simplified detective, we wait for the onPress block to stay active?
                                    // Actually, tryAwaitRelease is what we need.
                                    try {
                                        awaitRelease()
                                    } finally {
                                        // STOP recording
                                        val path = recorderHelper.stopRecording()
                                        timerJob?.cancel()
                                        isRecording = false
                                        timerText = "0:00"

                                        if (path != null) {
                                            recordedPaths[step] = path
                                            statusText = "Recorded! Play or proceed."
                                        } else {
                                            statusText = "Recording failed. Try again."
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        tint = SangatColors.Background,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                if (isRecording) "Recording... $timerText" else statusText,
                fontSize = 13.sp,
                color = if (isRecording) SangatColors.Violet else SangatColors.MutedForeground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(36.dp))

            // ── Playback Controls ──────────────────────────────
            if (recordedPaths[step] != null && !isRecording) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Play button
                    OutlinedButton(
                        onClick = {
                            recordedPaths[step]?.let { path ->
                                recorderHelper.playFile(path) { statusText = "Playback done." }
                                statusText = "Playing..."
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = SangatColors.NeonCyan)
                        Spacer(Modifier.width(4.dp))
                        Text("Play", color = SangatColors.NeonCyan)
                    }

                    // Re-record button
                    OutlinedButton(
                        onClick = {
                            recorderHelper.stopPlaying()
                            recorderHelper.deleteFile(eventKeys[step])
                            recordedPaths[step] = null
                            statusText = "Hold mic to record"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Replay, null, tint = SangatColors.MutedForeground)
                        Spacer(Modifier.width(4.dp))
                        Text("Re-record", color = SangatColors.MutedForeground)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.weight(1f))

            // ── Navigation Buttons ────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step--; statusText = if (recordedPaths[step] != null) "Recorded! Play or proceed." else "Hold mic to record" },
                        modifier = Modifier.weight(1f)
                    ) { Text("Back", color = SangatColors.Foreground) }
                }

                // Next / Done — only enabled if current step recorded
                Button(
                    onClick = {
                        recorderHelper.stopPlaying()
                        if (step < 5) {
                            step++
                            statusText = if (recordedPaths[step] != null) "Recorded! Play or proceed." else "Hold mic to record"
                        } else {
                            // All 6 done
                            val p0 = recordedPaths[0]
                            val p1 = recordedPaths[1]
                            val p2 = recordedPaths[2]
                            val p3 = recordedPaths[3]
                            val p4 = recordedPaths[4]
                            val p5 = recordedPaths[5]
                            if (p0 != null && p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) {
                                SharedPrefsHelper.saveAllPaths(context, p0, p1, p2) // Using existing saveAll for compatibility
                                // But we also need to save the new ones
                                SharedPrefsHelper.setRecordingPath(context, "rec_battery_low15", p3)
                                SharedPrefsHelper.setRecordingPath(context, "rec_battery_low5", p4)
                                SharedPrefsHelper.setRecordingPath(context, "rec_battery_overheat", p5)
                                
                                SharedPrefsHelper.setVoiceMode(context, 1) // OWN
                                recorderHelper.release()
                                onDone()
                            }
                        }
                    },
                    enabled = recordedPaths[step] != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SangatColors.NeonCyan)
                ) {
                    Text(
                        if (step == 5) "Finish" else "Next",
                        color = SangatColors.Background,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
}
