package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.QuayGuetApp
import com.example.ui.theme.MyApplicationTheme
import com.example.util.PlaybackNotificationHelper
import com.example.viewmodel.QuayGuetViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: QuayGuetViewModel by viewModels()

  private val mediaControlReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      when (intent?.action) {
        PlaybackNotificationHelper.ACTION_PLAY_PAUSE -> viewModel.togglePlayPause()
        PlaybackNotificationHelper.ACTION_NEXT -> viewModel.nextTrack()
        PlaybackNotificationHelper.ACTION_PREV -> viewModel.previousTrack()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val filter = IntentFilter().apply {
      addAction(PlaybackNotificationHelper.ACTION_PLAY_PAUSE)
      addAction(PlaybackNotificationHelper.ACTION_NEXT)
      addAction(PlaybackNotificationHelper.ACTION_PREV)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(mediaControlReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
      registerReceiver(mediaControlReceiver, filter)
    }

    setContent {
      MyApplicationTheme {
        QuayGuetApp(viewModel = viewModel)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      unregisterReceiver(mediaControlReceiver)
    } catch (e: Exception) {
      // Receiver may not have been registered
    }
  }
}

