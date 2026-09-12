package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.QuayGuetApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuayGuetViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: QuayGuetViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        QuayGuetApp(viewModel = viewModel)
      }
    }
  }
}
