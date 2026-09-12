package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = QuayGold,
    onPrimary = QuayGreenDark,
    primaryContainer = QuayGreen,
    onPrimaryContainer = SandWhite,
    secondary = QuayGoldLight,
    onSecondary = QuayGreenDark,
    tertiary = QuayRed,
    onTertiary = SandWhite,
    background = NightBackground,
    onBackground = SandWhite,
    surface = NightSurface,
    onSurface = SandWhite,
    surfaceVariant = NightCard,
    onSurfaceVariant = SandWhite.copy(alpha = 0.8f),
    outline = NightCardBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = QuayGreen,
    onPrimary = SandWhite,
    primaryContainer = QuayGreenContainer,
    onPrimaryContainer = QuayGreenDark,
    secondary = QuayGold,
    onSecondary = QuayGreenDark,
    tertiary = QuayRed,
    onTertiary = SandWhite,
    background = SandWhite,
    onBackground = QuayGreenDark,
    surface = SandSurface,
    onSurface = QuayGreenDark,
    surfaceVariant = SandCard,
    onSurfaceVariant = QuayGreen,
    outline = LightBorderSubtle
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
