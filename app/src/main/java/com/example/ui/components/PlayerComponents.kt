package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EventEntity
import com.example.data.model.TrackEntity
import com.example.player.PlaybackState
import com.example.ui.theme.*
import com.example.util.ShareHelper

@Composable
fun PersistentMiniPlayer(
    playbackState: PlaybackState,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    onLiveEventClick: (EventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return

    Surface(
        color = OceanBlueDark,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenFullPlayer() }
            .testTag("mini_player_container")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Linear progress indicator
            val progress = if (playbackState.durationSec > 0) {
                playbackState.currentPositionSec.toFloat() / playbackState.durationSec.toFloat()
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = NetYellow,
                trackColor = Color.White.copy(alpha = 0.2f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Art & Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.quay_guett_logo),
                            contentDescription = "Logo Quay Guett 221",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SandWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = track.artistName,
                                style = MaterialTheme.typography.bodySmall,
                                color = NetYellow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp
                            )
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = playbackState.quality,
                                    fontSize = 9.sp,
                                    color = SandWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Call To Action: "VOIR EN LIVE" if linked event exists!
                if (playbackState.linkedEvent != null) {
                    Button(
                        onClick = { onLiveEventClick(playbackState.linkedEvent) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CtaOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("mini_player_live_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VOIR EN LIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Controls: Play/Pause & Next
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                            tint = NetYellow,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            tint = SandWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerSheet(
    playbackState: PlaybackState,
    isProUser: Boolean = false,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onChangeQuality: (String) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onLiveEventClick: (EventEntity) -> Unit,
    onOpenArtistBio: ((String) -> Unit)? = null,
    onAddToPlaylist: ((TrackEntity) -> Unit)? = null,
    onOpenComments: ((TrackEntity) -> Unit)? = null,
    onOpenTipModal: ((TrackEntity) -> Unit)? = null,
    onOpenEqualizer: (() -> Unit)? = null
) {
    val track = playbackState.currentTrack ?: return
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showShareSnackbar by remember { mutableStateOf(false) }
    var showLyricsDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NightBackground,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.4f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Top title & Quality Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Fermer",
                        tint = SandWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "EN COURS DE LECTURE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SandWhite.copy(alpha = 0.7f)
                )

                // Quality selector pill (128k vs 320k vs FLAC)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NightSurface)
                        .padding(2.dp)
                ) {
                    QualityChip(
                        label = "128k",
                        isSelected = playbackState.quality == "LOW",
                        onClick = { onChangeQuality("LOW") }
                    )
                    QualityChip(
                        label = "320k",
                        isSelected = playbackState.quality == "HIGH",
                        onClick = { onChangeQuality("HIGH") }
                    )
                    QualityChip(
                        label = if (isProUser) "FLAC" else "FLAC ⭐",
                        isSelected = playbackState.quality == "FLAC",
                        onClick = { onChangeQuality("FLAC") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Disc Artwork with Vinyl/Waves aesthetic
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(OceanBlue, OceanBlueDark, NightBackground)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(OceanBlueDark),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.quay_guett_logo),
                            contentDescription = "Logo Quay Guett 221",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Track Title & Artist
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = SandWhite,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artistName + if (track.feat.isNotEmpty()) " ft. ${track.feat}" else "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NetYellow,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${track.genre} • ${track.city} (221)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SandWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seekbar & Time Codes
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = playbackState.currentPositionSec.toFloat(),
                    onValueChange = { onSeekTo(it.toInt()) },
                    valueRange = 0f..playbackState.durationSec.coerceAtLeast(1).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = NetYellow,
                        activeTrackColor = NetYellow,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(playbackState.currentPositionSec),
                        style = MaterialTheme.typography.bodySmall,
                        color = SandWhite.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatDuration(playbackState.durationSec),
                        style = MaterialTheme.typography.bodySmall,
                        color = SandWhite.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) NetYellow else SandWhite.copy(alpha = 0.4f)
                    )
                }

                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = SandWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }

                FilledIconButton(
                    onClick = onTogglePlayPause,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = NetYellow),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("full_player_play_btn")
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        tint = OceanBlueDark,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = SandWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(onClick = onToggleRepeat) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playbackState.isRepeat) NetYellow else SandWhite.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Community & Audio Features Row (Audiomack / Boomplay Style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NightSurface.copy(alpha = 0.6f))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Comments Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenComments?.invoke(track) }
                ) {
                    IconButton(
                        onClick = { onOpenComments?.invoke(track) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Commentaires",
                            tint = OceanBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Avis",
                        fontSize = 10.sp,
                        color = SandWhite.copy(alpha = 0.8f)
                    )
                }

                // Tip Artist Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenTipModal?.invoke(track) }
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NetYellow.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        IconButton(onClick = { onOpenTipModal?.invoke(track) }) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = "Pourboire",
                                tint = NetYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "Pourboire",
                        fontSize = 10.sp,
                        color = NetYellow,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Equalizer Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenEqualizer?.invoke() }
                ) {
                    IconButton(
                        onClick = { onOpenEqualizer?.invoke() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Égaliseur",
                            tint = if (playbackState.isEqualizerEnabled) CtaOrange else SandWhite.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (playbackState.isEqualizerEnabled) playbackState.equalizerProfile.name else "Égaliseur",
                        fontSize = 10.sp,
                        fontWeight = if (playbackState.isEqualizerEnabled) FontWeight.Bold else FontWeight.Normal,
                        color = if (playbackState.isEqualizerEnabled) CtaOrange else SandWhite.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Download Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onDownloadClick(track) }
                ) {
                    IconButton(
                        onClick = { onDownloadClick(track) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (track.isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                            contentDescription = "Télécharger",
                            tint = if (track.isDownloaded) SuccessGreen else SandWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (track.isDownloaded) "Prêt" else "Télécharger",
                        fontSize = 10.sp,
                        color = SandWhite.copy(alpha = 0.8f)
                    )
                }

                // Share WhatsApp Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        com.example.util.ShareHelper.shareTrackOnWhatsApp(
                            context = context,
                            title = track.title,
                            artist = track.artistName,
                            link = "https://quayguett221.sn/track/${track.id}"
                        )
                    }
                ) {
                    IconButton(
                        onClick = {
                            com.example.util.ShareHelper.shareTrackOnWhatsApp(
                                context = context,
                                title = track.title,
                                artist = track.artistName,
                                link = "https://quayguett221.sn/track/${track.id}"
                            )
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = SandWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Partager",
                        fontSize = 10.sp,
                        color = SandWhite.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // INNOVATION MAJEURE DU CAHIER DES CHARGES: CTA CONTEXTUEL 'VOIR EN LIVE'
            // =========================================================================
            if (playbackState.linkedEvent != null) {
                val event = playbackState.linkedEvent
                Surface(
                    color = NightCard,
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CtaOrange, NetYellow))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onLiveEventClick(event)
                        }
                        .testTag("full_player_linked_event_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CtaOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "🎤 CONCERT EN DIRECT LIÉ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NetYellow
                                )
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SandWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${event.venueName} • ${event.dateTimeText.take(18)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SandWhite.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onLiveEventClick(event)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CtaOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "RÉSERVER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quick Actions: Playlist, Artiste Bio, Paroles, WhatsApp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ajouter à une playlist
                FilledTonalButton(
                    onClick = {
                        onDismiss()
                        onAddToPlaylist?.invoke(track)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NightCard,
                        contentColor = SandWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = null,
                        tint = NetYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Playlist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Fiche Artiste & Bio
                FilledTonalButton(
                    onClick = {
                        onDismiss()
                        onOpenArtistBio?.invoke(track.artistId)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NightCard,
                        contentColor = SandWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = OceanBlueLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bio Artiste", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Paroles
                FilledTonalButton(
                    onClick = { showLyricsDialog = true },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NightCard,
                        contentColor = SandWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = CtaOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paroles", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // WhatsApp Direct Share
                FilledTonalButton(
                    onClick = { ShareHelper.shareTrack(context, track, toWhatsApp = true) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SuccessGreen.copy(alpha = 0.2f),
                        contentColor = SuccessGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Secondary Actions: Download and Share Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = { onDownloadClick(track) },
                    colors = ButtonDefaults.textButtonColors(contentColor = SandWhite.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = if (track.isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.FileDownload,
                        contentDescription = null,
                        tint = if (track.isDownloaded) SuccessGreen else NetYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (track.isDownloaded) "Téléchargé" else "Télécharger Offline",
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = { ShareHelper.shareTrack(context, track, toWhatsApp = false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = SandWhite.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = NetYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Partager lien", fontSize = 12.sp)
                }
            }

            if (showShareSnackbar) {
                Text(
                    text = "Lien copié: quayguet221.sn/track/${track.id}",
                    color = NetYellow,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // LYRICS DIALOG MODAL (Wolof & Français)
    if (showLyricsDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLyricsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NightSurface,
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NetYellow, OceanBlueLight))),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PAROLES & LYRICS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = NetYellow,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SandWhite
                            )
                            Text(
                                text = "${track.artistName} • Wolof & Français",
                                fontSize = 12.sp,
                                color = SandWhite.copy(alpha = 0.6f)
                            )
                        }

                        IconButton(onClick = { showLyricsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = SandWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(14.dp))

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        val displayLyrics = if (track.lyrics.isNotBlank()) {
                            track.lyrics
                        } else {
                            "Ndawrabine bi ci Quai Ndar,\nSunu coono, sunu mbégte ak mbër yi.\n\nSénégal sunu réew, nio ko bokk.\nDegg na ndaje ma ca pont Faidherbe ba ci Quai de pêche.\n\n(Paroles complètes synchronisées Quai Guett 221)"
                        }

                        Text(
                            text = displayLyrics,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 26.sp,
                                fontSize = 15.sp
                            ),
                            color = SandWhite,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            ShareHelper.shareTrack(context, track, toWhatsApp = true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Partager les paroles sur WhatsApp")
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) NetYellow else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) OceanBlueDark else SandWhite.copy(alpha = 0.6f)
        )
    }
}
