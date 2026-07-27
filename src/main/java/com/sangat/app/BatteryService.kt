package com.sangat.app

import android.app.*
import android.content.*
import android.media.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sangat.app.utils.SharedPrefsHelper
import java.io.File

class BatteryService : Service() {

    companion object {
        const val CHANNEL_ID     = "sangat_battery_channel"
        const val NOTIF_ID       = 1001
        const val TAG            = "BatteryService"
        const val ACTION_START   = "com.sangat.app.START"
        const val ACTION_STOP    = "com.sangat.app.STOP"
    }

    private var audioManager: AudioManager? = null
    private var mediaPlayer: MediaPlayer? = null
    private var originalVolume = 0
    private var wakeLock: PowerManager.WakeLock? = null

    // Tracking states for smart logic
    private var isCurrentlyFull = false
    private var lastBatteryFull = false
    private var lastLow15      = false
    private var lastLow5       = false
    private var lastOverheat   = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    stopLoop()
                    playVoice("charging_start")
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    if (isCurrentlyFull) {
                        // User unplugged while battery was 100%
                        // Stop the looping sound but do NOT play the disconnect sound
                        stopLoop()
                    } else {
                        // Normal unplug (not at 100%)
                        playVoice("charging_stop")
                    }
                    isCurrentlyFull = false
                    lastBatteryFull = false
                }
                Intent.ACTION_BATTERY_CHANGED -> processBatteryState(intent)
            }
        }
    }

    private fun processBatteryState(intent: Intent) {
        val level  = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale  = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                         status == BatteryManager.BATTERY_STATUS_FULL
        val percent = if (scale > 0) (level * 100 / scale) else 0

        // Check Full (100%)
        if (percent >= 100 && isCharging) {
            isCurrentlyFull = true
            if (!lastBatteryFull) {
                lastBatteryFull = true
                playVoice("battery_full", loop = true)
            }
        } else if (percent < 100) {
            isCurrentlyFull = false
            lastBatteryFull = false
            // Note: We don't stop the loop here automatically to ensure it continues
            // until the user actually unplugs the cable.
        }

        // Check Low Battery (15% and 5%)
        if (!isCharging) {
            if (percent <= 5) {
                if (!lastLow5) {
                    lastLow5 = true
                    playVoice("battery_low_5")
                }
            } else if (percent <= 15) {
                if (!lastLow15) {
                    lastLow15 = true
                    playVoice("battery_low_15")
                }
            }
        }
        
        if (percent > 15) {
            lastLow15 = false
            lastLow5  = false
        } else if (percent > 5) {
            lastLow5  = false
        }

        // Overheat
        if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            if (!lastOverheat) {
                lastOverheat = true
                playVoice("battery_overheat")
            }
        } else {
            lastOverheat = false
        }
    }

    // ── onCreate ──────────────────────────────────────────────
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
        acquireWakeLock()
    }

    // ── onStartCommand ────────────────────────────────────────
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, filter)
        Log.d(TAG, "BatteryService started")
        return START_STICKY
    }

    // ── onDestroy ─────────────────────────────────────────────
    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(batteryReceiver) } catch (e: Exception) { }
        releaseMediaPlayer()
        restoreVolume()
        releaseWakeLock()
        Log.d(TAG, "BatteryService stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── PLAY VOICE ────────────────────────────────────────────
    private fun playVoice(event: String, loop: Boolean = false) {
        val voiceMode = SharedPrefsHelper.getVoiceMode(this)

        if (voiceMode == 1) {
            // OWN voice — play user recording
            val path = when (event) {
                "charging_start" -> SharedPrefsHelper.getStartPath(this)
                "charging_stop"  -> SharedPrefsHelper.getStopPath(this)
                "battery_full"   -> SharedPrefsHelper.getFullPath(this)
                "battery_low_15" -> SharedPrefsHelper.getLow15Path(this)
                "battery_low_5"  -> SharedPrefsHelper.getLow5Path(this)
                "battery_overheat" -> SharedPrefsHelper.getOverheatPath(this)
                else             -> null
            }
            if (path != null && File(path).exists()) {
                playAudioFile(path, loop)
            } else {
                playAppDefault(event, loop)
            }
        } else {
            playAppDefault(event, loop)
        }
    }

    private fun playAppDefault(event: String, loop: Boolean = false) {
        val resId = when (event) {
            "charging_start"  -> R.raw.charging_start
            "charging_stop"   -> R.raw.charging_stop
            "battery_full"    -> R.raw.battery_full
            "battery_low_15"  -> R.raw.battery_low_15
            "battery_low_5"   -> R.raw.battery_low_5
            "battery_overheat" -> R.raw.battery_overheat
            else              -> return
        }
        duckAndPlay {
            releaseMediaPlayer()
            mediaPlayer = MediaPlayer.create(this, resId)?.apply {
                isLooping = loop
                if (!loop) {
                    setOnCompletionListener { restoreVolume(); releaseMediaPlayer() }
                }
                start()
            }
        }
    }

    private fun playAudioFile(path: String, loop: Boolean = false) {
        duckAndPlay {
            releaseMediaPlayer()
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(path)
                    prepare()
                    isLooping = loop
                    if (!loop) {
                        setOnCompletionListener { restoreVolume(); releaseMediaPlayer() }
                    }
                    start()
                } catch (e: Exception) {
                    Log.e(TAG, "MediaPlayer error: ${e.message}")
                    restoreVolume()
                }
            }
        }
    }

    // ── VOLUME DUCKING ────────────────────────────────────────
    // Set volume to BIG (80% of max) and duck other media
    private fun duckAndPlay(block: () -> Unit) {
        audioManager?.let { am ->
            // Save original volume
            originalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVol     = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            
            // Set media volume to 100% for MAX sound
            val targetVol = maxVol
            am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
        }
        block()
    }

    private fun restoreVolume() {
        try {
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
        } catch (e: Exception) { }
    }

    private fun releaseMediaPlayer() {
        try { mediaPlayer?.apply { if (isPlaying) stop(); release() } } catch (e: Exception) { }
        mediaPlayer = null
    }

    private fun stopLoop() {
        releaseMediaPlayer()
        restoreVolume()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SANGAT Battery Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows while SANGAT is monitoring battery"
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent     = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SANGAT")
            .setContentText("Monitoring battery...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SANGAT::BatteryWakeLock"
        ).apply { acquire(10 * 60 * 1000L) }
    }

    private fun releaseWakeLock() {
        try { wakeLock?.let { if (it.isHeld) it.release() } } catch (e: Exception) { }
        wakeLock = null
    }
}
