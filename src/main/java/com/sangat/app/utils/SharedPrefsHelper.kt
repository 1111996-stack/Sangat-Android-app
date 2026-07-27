package com.sangat.app.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsHelper {

    private const val PREFS_NAME = "sangat_prefs"

    // Keys
    private const val KEY_VOICE_MODE   = "voice_mode"       // 0=APP, 1=OWN
    private const val KEY_SERVICE_ON   = "service_on"       // boolean
    private const val KEY_SETUP_DONE   = "setup_done"       // boolean
    private const val KEY_REC_START    = "rec_charging_start"
    private const val KEY_REC_STOP     = "rec_charging_stop"
    private const val KEY_REC_FULL     = "rec_battery_full"
    private const val KEY_REC_LOW15    = "rec_battery_low15"
    private const val KEY_REC_LOW5     = "rec_battery_low5"
    private const val KEY_REC_OVERHEAT = "rec_battery_overheat"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Voice Mode
    fun setVoiceMode(context: Context, mode: Int) =
        prefs(context).edit().putInt(KEY_VOICE_MODE, mode).apply()

    fun getVoiceMode(context: Context): Int =
        prefs(context).getInt(KEY_VOICE_MODE, 0)

    // Service State
    fun setServiceOn(context: Context, on: Boolean) =
        prefs(context).edit().putBoolean(KEY_SERVICE_ON, on).apply()

    fun isServiceOn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SERVICE_ON, false)

    // Setup Status
    fun setSetupComplete(context: Context, complete: Boolean) =
        prefs(context).edit().putBoolean(KEY_SETUP_DONE, complete).apply()

    fun isSetupComplete(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SETUP_DONE, false)

    // Recording Paths
    fun setRecordingPath(context: Context, event: String, path: String) =
        prefs(context).edit().putString(event, path).apply()

    fun getRecordingPath(context: Context, event: String): String? =
        prefs(context).getString(event, null)

    fun getStartPath(context: Context)  = getRecordingPath(context, KEY_REC_START)
    fun getStopPath(context: Context)   = getRecordingPath(context, KEY_REC_STOP)
    fun getFullPath(context: Context)   = getRecordingPath(context, KEY_REC_FULL)
    fun getLow15Path(context: Context)  = getRecordingPath(context, KEY_REC_LOW15)
    fun getLow5Path(context: Context)   = getRecordingPath(context, KEY_REC_LOW5)
    fun getOverheatPath(context: Context) = getRecordingPath(context, KEY_REC_OVERHEAT)

    fun saveAllPaths(context: Context, start: String, stop: String, full: String) {
        prefs(context).edit()
            .putString(KEY_REC_START, start)
            .putString(KEY_REC_STOP, stop)
            .putString(KEY_REC_FULL, full)
            .apply()
    }

    // Delete all custom recordings
    fun deleteCustomRecordings(context: Context) {
        // Delete files from storage
        listOf(
            getStartPath(context),
            getStopPath(context),
            getFullPath(context)
        ).forEach { path ->
            if (path != null) {
                try { java.io.File(path).delete() } catch (e: Exception) { }
            }
        }
        // Clear paths from prefs
        prefs(context).edit()
            .remove(KEY_REC_START)
            .remove(KEY_REC_STOP)
            .remove(KEY_REC_FULL)
            .apply()
    }
}
