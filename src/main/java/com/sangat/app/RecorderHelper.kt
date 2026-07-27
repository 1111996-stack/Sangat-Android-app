package com.sangat.app

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class RecorderHelper(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentFilePath: String? = null
    var isRecording = false
        private set
    var isPlaying = false
        private set

    // Start recording — saves to internal storage
    // eventName = "charging_start" / "charging_stop" / "battery_full"
    fun startRecording(eventName: String): String? {
        stopRecording()
        stopPlaying()

        val file = File(context.filesDir, "$eventName.m4a")
        currentFilePath = file.absolutePath

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        try {
            recorder!!.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(currentFilePath)
                prepare()
                start()
            }
            isRecording = true
            return currentFilePath
        } catch (e: IOException) {
            e.printStackTrace()
            isRecording = false
            return null
        }
    }

    // Stop recording — returns file path
    fun stopRecording(): String? {
        if (!isRecording) return currentFilePath
        try {
            recorder?.apply { stop(); release() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        isRecording = false
        return currentFilePath
    }

    // Play a recorded file
    fun playFile(filePath: String, onComplete: () -> Unit = {}) {
        stopPlaying()
        if (!File(filePath).exists()) return

        player = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
                this@RecorderHelper.isPlaying = true
                setOnCompletionListener {
                    this@RecorderHelper.isPlaying = false
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                this@RecorderHelper.isPlaying = false
            }
        }
    }

    // Stop playing
    fun stopPlaying() {
        try { player?.apply { if (isPlaying) stop(); release() } } catch (e: Exception) { }
        player = null
        isPlaying = false
    }

    // Delete recording file
    fun deleteFile(eventName: String) {
        try { File(context.filesDir, "$eventName.m4a").delete() } catch (e: Exception) { }
    }

    // Check if recording exists
    fun recordingExists(eventName: String): Boolean =
        File(context.filesDir, "$eventName.m4a").exists()

    fun getFilePath(eventName: String): String =
        File(context.filesDir, "$eventName.m4a").absolutePath

    // Release all resources
    fun release() {
        stopRecording()
        stopPlaying()
    }
}
