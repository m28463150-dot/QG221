package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EqualizerProfile
import com.example.data.model.TrackCommentEntity
import com.example.data.model.TrackEntity
import com.example.ui.theme.*
import java.util.Locale

// ==========================================
// 1. ESPACE COMMENTAIRES ET RÉACTIONS
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackCommentsSheet(
    track: TrackEntity,
    comments: List<TrackCommentEntity>,
    currentTrackPositionSec: Int,
    onDismiss: () -> Unit,
    onPostComment: (text: String, timestampSec: Int, timestampText: String) -> Unit,
    onLikeComment: (commentId: String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var attachTimestamp by remember { mutableStateOf(false) }

    val formattedCurrentPos = remember(currentTrackPositionSec) {
        val m = currentTrackPositionSec / 60
        val s = currentTrackPositionSec % 60
        String.format("%02d:%02d", m, s)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("track_comments_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = OceanBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Commentaires & Réactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "« ${track.title} » • ${comments.size} réaction(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Soyez le premier à commenter ce titre !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItemRow(
                            comment = comment,
                            onLike = { onLikeComment(comment.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Quick Emojis
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val emojis = listOf("🔥", "❤️", "🇸🇳", "👏", "🎯", "🌊", "👑", "🚀")
                items(emojis) { emoji ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = CircleShape,
                        modifier = Modifier.clickable {
                            commentText += emoji
                        }
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp reaction toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = attachTimestamp,
                    onClick = { attachTimestamp = !attachTimestamp },
                    label = {
                        Text(
                            text = if (attachTimestamp) "Position liée : $formattedCurrentPos" else "Lier à la lecture ($formattedCurrentPos)",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OceanBlue.copy(alpha = 0.2f),
                        selectedLabelColor = OceanBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text("Partagez votre avis sur ce son...", fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilledIconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            val timeSec = if (attachTimestamp) currentTrackPositionSec else 0
                            val timeTxt = if (attachTimestamp) formattedCurrentPos else ""
                            onPostComment(commentText, timeSec, timeTxt)
                            commentText = ""
                            attachTimestamp = false
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = OceanBlue),
                    modifier = Modifier.testTag("send_comment_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Envoyer", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CommentItemRow(
    comment: TrackCommentEntity,
    onLike: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.authorName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = OceanBlue
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = comment.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (comment.timestampText.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = OceanBlue.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = comment.timestampText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlue,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Like action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLike() }
                    ) {
                        Icon(
                            imageVector = if (comment.likesCount > 0) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (comment.likesCount > 0) CtaOrange else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                        if (comment.likesCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${comment.likesCount}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ==========================================
// 2. POURBOIRES ET SOUTIEN DIRECT AUX ARTISTES
// ==========================================
@Composable
fun TipArtistDialog(
    track: TrackEntity,
    onDismiss: () -> Unit,
    onSendTip: (amountCfa: Int, provider: String, message: String) -> Unit
) {
    var selectedAmount by remember { mutableStateOf(1000) }
    var selectedProvider by remember { mutableStateOf("Wave") }
    var messageText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    tint = NetYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Soutenir l'Artiste",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "100% de votre pourboire est reversé directement à ${track.artistName} sur son compte mobile sénégalais.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Amount Chips
                Text(
                    text = "Montant du pourboire (FCFA)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val amounts = listOf(500, 1000, 2500, 5000)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    amounts.forEach { amount ->
                        val isSelected = selectedAmount == amount
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NetYellow else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedAmount = amount }
                        ) {
                            Text(
                                text = "${amount}F",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) OceanBlueDark else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                // Payment Provider
                Text(
                    text = "Moyen de paiement",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val providers = listOf("Wave", "Orange Money")
                    providers.forEach { provider ->
                        val isSelected = selectedProvider == provider
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                if (provider == "Wave") Color(0xFF1E88E5) else Color(0xFFFF6D00)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = provider }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (provider == "Wave") Icons.Default.Waves else Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = provider,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Message for artist
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = {
                        Text("Un mot d'encouragement pour ${track.artistName}...", fontSize = 12.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSendTip(selectedAmount, selectedProvider, messageText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NetYellow, contentColor = OceanBlueDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Envoyer $selectedAmount F ($selectedProvider)",
                    fontWeight = FontWeight.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// ==========================================
// 3. ÉGALISEUR AUDIO GRAPHIQUE 5 BANDES (BASS, MID, TREBLE) & PRESETS
// ==========================================

data class BandMetadata(
    val index: Int,
    val frequencyLabel: String,
    val category: String, // "BASS", "MID", "TREBLE"
    val subLabel: String,
    val color: Color
)

val FREQUENCY_BANDS = listOf(
    BandMetadata(0, "60 Hz", "BASS", "Sub & Ndënd", CtaOrange),
    BandMetadata(1, "230 Hz", "BASS", "Punch & Gorong", Color(0xFFFF9800)),
    BandMetadata(2, "910 Hz", "MID", "Voix & Kora", OceanBlue),
    BandMetadata(3, "3.6 kHz", "MID", "Clarté & Tama", Color(0xFF00BCD4)),
    BandMetadata(4, "14 kHz", "TREBLE", "Aigus & Air", SuccessGreen)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEqualizerSheet(
    isEqualizerEnabled: Boolean,
    currentProfile: EqualizerProfile,
    bassBoostPercent: Int,
    onDismiss: () -> Unit,
    onToggleEqualizer: () -> Unit,
    onSelectProfile: (EqualizerProfile) -> Unit,
    onBandGainChange: (bandIndex: Int, gainDb: Float) -> Unit = { _, _ -> },
    onResetBands: () -> Unit = {},
    onBassBoostChange: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NightBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.4f)) },
        modifier = Modifier.testTag("audio_equalizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Power Switch & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isEqualizerEnabled) OceanBlue.copy(alpha = 0.2f)
                                else Color.White.copy(alpha = 0.08f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Égaliseur",
                            tint = if (isEqualizerEnabled) CtaOrange else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Égaliseur Graphique 5 Bandes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SandWhite
                        )
                        Text(
                            text = "Basses Sabar • Médiums & Voix • Aigus Clairs",
                            style = MaterialTheme.typography.bodySmall,
                            color = SandWhite.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Reset Button
                    IconButton(
                        onClick = onResetBands,
                        enabled = isEqualizerEnabled,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Réinitialiser",
                            tint = if (isEqualizerEnabled) SandWhite.copy(alpha = 0.8f) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isEqualizerEnabled,
                        onCheckedChange = { onToggleEqualizer() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = OceanBlue,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Horizontal Carousel (Mbalax, Hip-Hop, Acoustique, etc.)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Préréglages Sonores",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SandWhite.copy(alpha = 0.9f)
                )
                Text(
                    text = if (isEqualizerEnabled) "Actif : ${currentProfile.name}" else "Égaliseur Désactivé",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isEqualizerEnabled) NetYellow else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(EqualizerProfile.PRESETS) { profile ->
                    val isSelected = currentProfile.id == profile.id
                    val presetIcon = when (profile.id) {
                        "mbalax" -> "🥁"
                        "hiphop" -> "🎧"
                        "acoustique" -> "🪕"
                        "vocal" -> "🎙️"
                        "flat" -> "⚖️"
                        else -> "🎛️"
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isEqualizerEnabled) {
                                onSelectProfile(profile)
                            }
                        },
                        enabled = isEqualizerEnabled,
                        label = {
                            Text(
                                text = "$presetIcon ${profile.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OceanBlue,
                            selectedLabelColor = Color.White,
                            containerColor = NightSurface,
                            labelColor = SandWhite.copy(alpha = 0.8f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = isEqualizerEnabled,
                            selected = isSelected,
                            borderColor = if (isSelected) OceanBlue else Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }

            // Contextual Sound Design Description
            val profileDescription = when (currentProfile.id) {
                "mbalax" -> "🥁 Mbalax : Dynamique Sabar et Gorong boostée (+5 dB), clarté des claquements de Tama et brillance rythmique."
                "hiphop" -> "🎧 Hip-Hop : Sub-bass 808 ultra-profonde (+7 dB), bas-médiums creusés et présence vocale percutante pour le rap galsen."
                "acoustique" -> "🪕 Acoustique : Chaleur des cordes (kora, guitare sèche) et voix limpides avec basses douces de Saint-Louis."
                "vocal" -> "🎙️ Vocal : Fréquences de voix amplifiées (+4 dB) et basses atténuées pour une parfaite intelligibilité des paroles."
                "flat" -> "⚖️ Équilibré (Studio) : Réponse neutre et linéaire sans coloration pour une écoute fidèle au master original."
                else -> "🎛️ Personnalisé : Courbe sur mesure ajustée manuellement bande par bande selon vos préférences."
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = NightSurface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = profileDescription,
                    fontSize = 11.sp,
                    color = SandWhite.copy(alpha = 0.75f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Graphical Equalizer Visualizer (Courbe Graphique Spline)
            Surface(
                color = NightSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPECTRE GRAPHIQUE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SandWhite.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CtaOrange))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bass", fontSize = 10.sp, color = SandWhite.copy(alpha = 0.6f))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OceanBlue))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mid", fontSize = 10.sp, color = SandWhite.copy(alpha = 0.6f))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Treble", fontSize = 10.sp, color = SandWhite.copy(alpha = 0.6f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    EqualizerCurveCanvas(
                        bandsDb = currentProfile.bandsDb,
                        isEnabled = isEqualizerEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5 INTERACTIVE BANDS (Bass, Mid, Treble)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajustement des 5 Bandes (-12 dB à +12 dB)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SandWhite
                )
                Text(
                    text = "Glissez ou touchez",
                    fontSize = 10.sp,
                    color = SandWhite.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5 Vertical Slider Controls
            Surface(
                color = NightSurface.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FREQUENCY_BANDS.forEach { band ->
                        val gain = currentProfile.bandsDb.getOrElse(band.index) { 0f }
                        VerticalBandSlider(
                            bandIndex = band.index,
                            frequencyLabel = band.frequencyLabel,
                            categoryTag = band.category,
                            subLabel = band.subLabel,
                            gainDb = gain,
                            accentColor = band.color,
                            isEnabled = isEqualizerEnabled,
                            onGainChange = { newGain ->
                                onBandGainChange(band.index, newGain)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bass Boost Card (Sabar & Sub-Bass)
            Surface(
                color = NightSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CtaOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = CtaOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Amplification des Basses (Bass Boost)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SandWhite
                                )
                                Text(
                                    text = "Impact profond des tambours Sabar & 808",
                                    fontSize = 11.sp,
                                    color = SandWhite.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CtaOrange.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "$bassBoostPercent%",
                                fontWeight = FontWeight.ExtraBold,
                                color = CtaOrange,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = bassBoostPercent.toFloat(),
                        onValueChange = { onBassBoostChange(it.toInt()) },
                        valueRange = 0f..100f,
                        enabled = isEqualizerEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = CtaOrange,
                            activeTrackColor = CtaOrange,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    }
}

// ==========================================
// CANVASES & SLIDERS INTERACTIFS ÉGALISEUR
// ==========================================

@Composable
fun EqualizerCurveCanvas(
    bandsDb: List<Float>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val paddingX = 24.dp.toPx()
        val paddingY = 16.dp.toPx()
        val drawHeight = h - 2 * paddingY
        val drawWidth = w - 2 * paddingX
        val midY = h / 2f

        // Draw horizontal grid lines: +12dB, +6dB, 0dB, -6dB, -12dB
        val dbLevels = listOf(12f, 6f, 0f, -6f, -12f)
        dbLevels.forEach { db ->
            val y = midY - (db / 12f) * (drawHeight / 2f)
            val isCenter = db == 0f
            drawLine(
                color = if (isCenter) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                start = Offset(paddingX, y),
                end = Offset(w - paddingX, y),
                strokeWidth = if (isCenter) 1.5.dp.toPx() else 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        val stepX = drawWidth / 4f
        val points = (0 until 5).map { index ->
            val x = paddingX + index * stepX
            val rawDb = bandsDb.getOrElse(index) { 0f }
            val clampedDb = if (isEnabled) rawDb.coerceIn(-12f, 12f) else 0f
            val y = midY - (clampedDb / 12f) * (drawHeight / 2f)
            Offset(x, y)
        }

        // Build smooth spline curve
        val curvePath = Path()
        val fillPath = Path()

        if (points.isNotEmpty()) {
            curvePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, h)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2f
                curvePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, h)
            fillPath.close()

            // Fill gradient under curve
            if (isEnabled) {
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            OceanBlue.copy(alpha = 0.45f),
                            NetYellow.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        startY = paddingY,
                        endY = h
                    )
                )
            }

            // Stroke curve
            val curveBrush = if (isEnabled) {
                Brush.horizontalGradient(
                    colors = listOf(CtaOrange, OceanBlue, SuccessGreen),
                    startX = paddingX,
                    endX = w - paddingX
                )
            } else {
                Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f)))
            }

            drawPath(
                path = curvePath,
                brush = curveBrush,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw glowing node circles on top of each frequency band
            points.forEachIndexed { idx, point ->
                val nodeColor = when (idx) {
                    0, 1 -> CtaOrange
                    2, 3 -> OceanBlue
                    else -> SuccessGreen
                }
                if (isEnabled) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.35f),
                        radius = 7.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = nodeColor,
                        radius = 2.5.dp.toPx(),
                        center = point
                    )
                } else {
                    drawCircle(
                        color = Color.Gray,
                        radius = 3.dp.toPx(),
                        center = point
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalBandSlider(
    bandIndex: Int,
    frequencyLabel: String,
    categoryTag: String, // "BASS", "MID", "TREBLE"
    subLabel: String,
    gainDb: Float,
    accentColor: Color,
    isEnabled: Boolean,
    onGainChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        // Category Tag Badge (BASS / MID / TREBLE)
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isEnabled) accentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = categoryTag,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isEnabled) accentColor else Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // dB Gain Value text
        val formattedGain = if (gainDb > 0) "+${String.format(Locale.US, "%.1f", gainDb)}"
        else String.format(Locale.US, "%.1f", gainDb)

        Text(
            text = "$formattedGain dB",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isEnabled) accentColor else Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Custom Vertical Touch Slider
        val trackHeight = 110.dp
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(trackHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(NightSurface)
                .border(
                    width = 1.dp,
                    color = if (isEnabled) accentColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(20.dp)
                )
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectTapGestures(
                        onDoubleTap = {
                            onGainChange(0f)
                        },
                        onTap = { offset ->
                            val heightPx = size.height
                            val fraction = (offset.y / heightPx).coerceIn(0f, 1f)
                            val newGain = (1f - fraction) * 24f - 12f
                            onGainChange((Math.round(newGain * 2f) / 2f).coerceIn(-12f, 12f))
                        }
                    )
                }
                .pointerInput(isEnabled, gainDb) {
                    if (!isEnabled) return@pointerInput
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val deltaDb = -(dragAmount.y / 4.0f)
                        val newGain = (gainDb + deltaDb).coerceIn(-12f, 12f)
                        onGainChange((Math.round(newGain * 2f) / 2f))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Background Vertical Rail
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            // Center Reference Notch (0 dB)
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.35f))
            )

            // Draggable Thumb handle
            val thumbNormalized = ((12f - gainDb) / 24f).coerceIn(0.04f, 0.96f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isEnabled) accentColor else Color.Gray,
                    shadowElevation = if (isEnabled) 4.dp else 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .size(width = 32.dp, height = 18.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (trackHeight - 18.dp) * thumbNormalized)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(modifier = Modifier.width(1.5.dp).height(8.dp).background(Color.White.copy(alpha = 0.85f)))
                            Box(modifier = Modifier.width(1.5.dp).height(8.dp).background(Color.White.copy(alpha = 0.85f)))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // +/- Quick micro-step buttons for tap precision
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onGainChange((gainDb - 1f).coerceIn(-12f, 12f)) },
                enabled = isEnabled,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "-1 dB",
                    tint = if (isEnabled) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.size(12.dp)
                )
            }
            IconButton(
                onClick = { onGainChange((gainDb + 1f).coerceIn(-12f, 12f)) },
                enabled = isEnabled,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "+1 dB",
                    tint = if (isEnabled) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Frequency Label
        Text(
            text = frequencyLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SandWhite
        )
        Text(
            text = subLabel,
            fontSize = 9.sp,
            color = SandWhite.copy(alpha = 0.6f)
        )
    }
}

