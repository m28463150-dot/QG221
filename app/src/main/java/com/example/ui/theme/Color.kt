package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// QUAY GUETT 221 Official Logo Colors (Sénégal - Vert, Or, Rouge & Baobab Fréquences)
// 1. Vert Émeraude Sénégal (Calligraphie, Étoile centrale, Fréquences hautes)
val QuayGreen = Color(0xFF0C6B37)
val QuayGreenDark = Color(0xFF074825)
val QuayGreenLight = Color(0xFF1E874B)
val QuayGreenContainer = Color(0xFFE2F4E9)
val QuayGreenSurface = Color(0xFFF0F8F3)

// 2. Or Solaire Sénégal (Canopée de fréquences, Branches d'or, Rayonnement)
val QuayGold = Color(0xFFF9B81B)
val QuayGoldDark = Color(0xFFD49204)
val QuayGoldLight = Color(0xFFFDE68A)
val QuayGoldContainer = Color(0xFFFFF9E6)

// 3. Rouge Vermillon Sénégal (Fréquences basses, Entrelacs et rubans dynamiques)
val QuayRed = Color(0xFFD92D20)
val QuayRedDark = Color(0xFFB42318)
val QuayRedLight = Color(0xFFFCA5A5)
val QuayRedContainer = Color(0xFFFEF3F2)

// Mappage thématique direct vers les couleurs du Logo
val OceanBlue = QuayGreen
val OceanBlueDark = QuayGreenDark
val OceanBlueLight = QuayGreenLight

val NetYellow = QuayGold
val NetYellowDark = QuayGoldDark
val NetYellowLight = QuayGoldLight

val CtaOrange = QuayRed
val CtaOrangeDark = QuayRedDark

val SandWhite = Color(0xFFFDFEFE)
val SandSurface = Color(0xFFFFFFFF)
val SandCard = Color(0xFFF4F9F6)

val NightBackground = Color(0xFF071E12)
val NightSurface = Color(0xFF0D2E1C)
val NightCard = Color(0xFF133F27)
val NightCardBorder = Color(0xFF1D5A38)

val SuccessGreen = Color(0xFF12B76A)
val ErrorRed = QuayRed
val WaveCyan = Color(0xFF1BA3E3)
val OrangeMoney = Color(0xFFFF7900)

// QUAY Clair Dégradé aux couleurs du Logo (Vert doux -> Blanc -> Or chaud)
val LightCanvasTop = Color(0xFFF0F8F3)
val LightCanvasMid = Color(0xFFFAFCFA)
val LightCanvasBottom = Color(0xFFFFFDF5)
val LightSurfaceGradStart = Color(0xFFFFFFFF)
val LightSurfaceGradEnd = Color(0xFFF2F8F4)
val LightBorderSubtle = Color(0xFFE1EFE6)

// QUAY PRO Brand Colors & Gradients
val ProGold = Color(0xFFFFD700)
val ProGoldDark = Color(0xFFB8860B)
val ProAmber = Color(0xFFFF9F43)
val ProDeepPurple = Color(0xFF4A154B)
val ProCrimson = QuayRed

val QuayLightGradColors = listOf(LightCanvasTop, LightCanvasMid, LightCanvasBottom)
val QuayCardLightGradColors = listOf(LightSurfaceGradStart, LightSurfaceGradEnd)
val QuayProGradColors = listOf(ProGold, ProAmber, QuayRed)
val QuayOceanGradColors = listOf(QuayGreen, QuayGreenLight)
val QuaySenegalGradColors = listOf(QuayGreen, QuayGold, QuayRed)


