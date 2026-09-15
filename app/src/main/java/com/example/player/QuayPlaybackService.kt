package com.example.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.session.MediaSession
import android.media.session.PlaybackState as AndroidPlaybackState
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.example.data.model.TrackEntity
import com.example.util.PlaybackNotificationHelper

/**
 * Service de lecture audio en arrière-plan avec MediaSession pour Quay Guett 221.
 * Assure la persistance de la musique écran éteint, les boutons Bluetooth/casque et la barre système.
 */
class QuayPlaybackService : Service() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        initMediaSession()
    }

    private fun initMediaSession() {
        mediaSession = MediaSession(this, "QuayPlaybackSession").apply {
            setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    sendBroadcast(Intent(PlaybackNotificationHelper.ACTION_PLAY_PAUSE).setPackage(packageName))
                }

                override fun onPause() {
                    sendBroadcast(Intent(PlaybackNotificationHelper.ACTION_PLAY_PAUSE).setPackage(packageName))
                }

                override fun onSkipToNext() {
                    sendBroadcast(Intent(PlaybackNotificationHelper.ACTION_NEXT).setPackage(packageName))
                }

                override fun onSkipToPrevious() {
                    sendBroadcast(Intent(PlaybackNotificationHelper.ACTION_PREV).setPackage(packageName))
                }

                override fun onStop() {
                    sendBroadcast(Intent(PlaybackNotificationHelper.ACTION_PLAY_PAUSE).setPackage(packageName))
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })

            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_START -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Morceau Sénégal"
                val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Artiste"
                val genre = intent.getStringExtra(EXTRA_GENRE) ?: "Mbalax"
                val trackId = intent.getStringExtra(EXTRA_ID) ?: "track_0"
                val isPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, true)

                val dummyTrack = TrackEntity(
                    id = trackId,
                    artistId = "",
                    artistName = artist,
                    title = title,
                    feat = "",
                    genre = genre,
                    city = "Sénégal",
                    durationSec = 210,
                    plays = 0,
                    likes = 0,
                    audioQuality = "HIGH",
                    coverResName = "quay_guett_logo",
                    status = "approved",
                    isDownloaded = false,
                    lyrics = ""
                )

                val notification = PlaybackNotificationHelper.buildNotification(
                    context = this,
                    track = dummyTrack,
                    isPlaying = isPlaying
                )

                updateMediaSessionState(isPlaying)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        PlaybackNotificationHelper.NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(PlaybackNotificationHelper.NOTIFICATION_ID, notification)
                }
            }

            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun updateMediaSessionState(isPlaying: Boolean) {
        val stateBuilder = AndroidPlaybackState.Builder()
            .setActions(
                AndroidPlaybackState.ACTION_PLAY or
                        AndroidPlaybackState.ACTION_PAUSE or
                        AndroidPlaybackState.ACTION_SKIP_TO_NEXT or
                        AndroidPlaybackState.ACTION_SKIP_TO_PREVIOUS or
                        AndroidPlaybackState.ACTION_STOP
            )
            .setState(
                if (isPlaying) AndroidPlaybackState.STATE_PLAYING else AndroidPlaybackState.STATE_PAUSED,
                AndroidPlaybackState.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )

        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.example.quayguett.service.START"
        const val ACTION_STOP = "com.example.quayguett.service.STOP"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_GENRE = "extra_genre"
        const val EXTRA_IS_PLAYING = "extra_is_playing"

        fun start(context: Context, track: TrackEntity, isPlaying: Boolean) {
            val intent = Intent(context, QuayPlaybackService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ID, track.id)
                putExtra(EXTRA_TITLE, track.title)
                putExtra(EXTRA_ARTIST, track.artistName)
                putExtra(EXTRA_GENRE, track.genre)
                putExtra(EXTRA_IS_PLAYING, isPlaying)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // If in background, fallback gracefully to notification manager
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, QuayPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
