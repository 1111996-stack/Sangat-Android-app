package com.sangat.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sangat.app.ui.screens.*
import com.sangat.app.ui.theme.SangatColors
import com.sangat.app.ui.theme.SangatTheme
import com.sangat.app.utils.SharedPrefsHelper

private enum class Screen { Splash, Perms, Option, Main }

class MainActivity : ComponentActivity() {

    // ── Permission launcher ───────────────────────────────────
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // All permissions handled — proceed regardless
        navigateToOption()
    }

    private var onPermsDone: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SangatTheme {
                Surface(Modifier.fillMaxSize(), color = SangatColors.Background) {
                    SangatApp(
                        onRequestPermissions = { callback ->
                            onPermsDone = callback
                            requestAllPermissions()
                        },
                        onServiceToggle = { on -> toggleService(on) }
                    )
                }
            }
        }
    }

    private fun requestAllPermissions() {
        val perms = mutableListOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(perms.toTypedArray())
        requestBatteryOptimizationExemption()
    }

    private fun requestBatteryOptimizationExemption() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        } catch (e: Exception) { }
    }

    private fun navigateToOption() {
        onPermsDone?.invoke()
        onPermsDone = null
    }

    fun toggleService(on: Boolean) {
        SharedPrefsHelper.setServiceOn(this, on)
        val intent = Intent(this, BatteryService::class.java)
        if (on) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } else {
            stopService(intent)
        }
    }
}

@Composable
private fun SangatApp(
    onRequestPermissions: (callback: () -> Unit) -> Unit,
    onServiceToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Logic: If setup is complete, start at Main. Else start at Splash.
    val initialScreen = if (SharedPrefsHelper.isSetupComplete(context)) Screen.Main else Screen.Splash
    
    var screen      by remember { mutableStateOf(initialScreen) }
    var mode        by remember { mutableStateOf(if (SharedPrefsHelper.getVoiceMode(context) == 1) VoiceMode.OWN else VoiceMode.APP) }
    var serviceOn   by remember { mutableStateOf(SharedPrefsHelper.isServiceOn(context)) }
    var showRecorder by remember { mutableStateOf(false) }
    var showChange  by remember { mutableStateOf(false) }

    when (screen) {
        Screen.Splash -> SplashScreen(onStart = { screen = Screen.Perms })

        Screen.Perms  -> PermissionSheet(onAllow = {
            onRequestPermissions { screen = Screen.Option }
        })

        Screen.Option -> OptionScreen(onPick = { pickedMode ->
            if (pickedMode == VoiceMode.OWN) {
                showRecorder = true
            } else {
                SharedPrefsHelper.setVoiceMode(context, 0)
                SharedPrefsHelper.setSetupComplete(context, true)
                mode = VoiceMode.APP
                screen = Screen.Main
            }
        })

        Screen.Main -> MainScreen(
            mode       = mode,
            serviceOn  = serviceOn,
            onToggleService = { on ->
                serviceOn = on
                onServiceToggle(on)
            },
            onChangeVoice = { showChange = true }
        )
    }

    if (showRecorder) {
        RecorderWizard(
            onClose = {
                showRecorder = false
                if (screen != Screen.Main) {
                    // If they closed recorder during initial setup, stay on Option
                }
            },
            onDone = {
                showRecorder = false
                SharedPrefsHelper.setSetupComplete(context, true)
                mode = VoiceMode.OWN
                screen = Screen.Main
            }
        )
    }

    if (showChange) {
        ChangeVoiceDialog(
            currentMode  = mode,
            onClose      = { showChange = false },
            onConfirmApp = {
                SharedPrefsHelper.deleteCustomRecordings(context)
                SharedPrefsHelper.setVoiceMode(context, 0)
                mode = VoiceMode.APP
                showChange = false
            },
            onConfirmOwn = { showChange = false; showRecorder = true }
        )
    }
}
