package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.TrackEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import java.security.MessageDigest

@Composable
fun QuayTopBar(
    currentUser: UserEntity,
    isOfflineMode: Boolean,
    isProUser: Boolean = false,
    onToggleOffline: () -> Unit,
    onSwitchUserRole: (String) -> Unit,
    onOpenPro: (() -> Unit)? = null
) {
    var showUserMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF3F7FB),
                        Color(0xFFFFFDF7)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, QuayGreen.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.quay_guett_logo),
                            contentDescription = "Logo Officiel Quay Guett 221",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "QUAY GUETT 221",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = QuayGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isProUser) {
                                Surface(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .background(
                                            Brush.horizontalGradient(listOf(ProGold, ProAmber)),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onOpenPro?.invoke() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = OceanBlueDark,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "PRO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = OceanBlueDark
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    color = Color(0xFFFFF0D4),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ProGoldDark.copy(alpha = 0.5f)),
                                    modifier = Modifier.clickable { onOpenPro?.invoke() }
                                ) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ProGoldDark,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isProUser) "⭐ Membre PRO • Jekk naa ci Quay bi" else "Jekk naa ci Quay bi",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isProUser) ProGoldDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = if (isProUser) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }

                // Actions: Offline toggle & User selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Offline badge toggle (for persona Modou with limited data!)
                    FilterChip(
                        selected = isOfflineMode,
                        onClick = onToggleOffline,
                        label = {
                            Text(
                                text = if (isOfflineMode) "Offline" else "Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.Wifi,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CtaOrange.copy(alpha = 0.2f),
                            selectedLabelColor = CtaOrange,
                            selectedLeadingIconColor = CtaOrange
                        ),
                        modifier = Modifier.testTag("offline_toggle_chip")
                    )

                    // Role switch button (Auditeur, Artiste, Vigile, Admin)
                    Box {
                        FilledTonalButton(
                            onClick = { showUserMenu = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isProUser) ProGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("user_role_switch_btn")
                        ) {
                            Icon(
                                imageVector = when (currentUser.role) {
                                    "artist" -> Icons.Default.Mic
                                    "vigil" -> Icons.Default.QrCodeScanner
                                    "admin" -> Icons.Default.Shield
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = if (isProUser) ProGoldDark else OceanBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "@" + currentUser.username.take(7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isProUser) ProGoldDark else OceanBlue
                            )
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isProUser) "⭐ Statut : Membre PRO Actif" else "⭐ Découvrir la Version PRO") },
                                onClick = {
                                    onOpenPro?.invoke()
                                    showUserMenu = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("👤 Modou (Auditeur / Fan)") },
                                onClick = {
                                    onSwitchUserRole("listener")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🎤 Awa Ndar (Artiste Vérifiée)") },
                                onClick = {
                                    onSwitchUserRole("artist")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🛡️ Vigile (Contrôle Billetterie)") },
                                onClick = {
                                    onSwitchUserRole("vigil")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️ Admin (Back-Office)") },
                                onClick = {
                                    onSwitchUserRole("admin")
                                    showUserMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (isOfflineMode) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = CtaOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AirplanemodeActive,
                            contentDescription = null,
                            tint = CtaOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mode hors-connexion activé - Écoute gratuite des titres téléchargés",
                            fontSize = 11.sp,
                            color = CtaOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRowItem(
    track: TrackEntity,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    hasLiveEvent: Boolean = false,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onLiveEventClick: (() -> Unit)? = null
) {
    Surface(
        color = if (isCurrentTrack) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isCurrentTrack) 3.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() }
            .testTag("track_item_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Track Art / Index & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCurrentTrack) NetYellow else OceanBlue
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCurrentTrack && isPlaying) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Playing",
                            tint = if (isCurrentTrack) OceanBlueDark else SandWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = if (isCurrentTrack) OceanBlueDark else NetYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isCurrentTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = track.artistName + if (track.feat.isNotEmpty()) " ft. ${track.feat}" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "• ${track.genre}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }

                    // Contextual Live concert badge!
                    if (hasLiveEvent) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            color = CtaOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.clickable { onLiveEventClick?.invoke() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = CtaOrange,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "EN LIVE DISPO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CtaOrange
                                )
                            }
                        }
                    }
                }
            }

            // Right actions: Download button & Duration
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (track.isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.FileDownload,
                        contentDescription = if (track.isDownloaded) "Téléchargé" else "Télécharger pour écoute offline",
                        tint = if (track.isDownloaded) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = formatDuration(track.durationSec),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

// Generate realistic QR code matrix representation on Compose Canvas without external heavy libs
@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Billet QR Code"
) {
    // Generate deterministic 21x21 QR-like matrix based on SHA-256
    val hash = remember(data) {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(data.toByteArray())
        bytes
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizeCount = 21
            val cellW = size.width / sizeCount
            val cellH = size.height / sizeCount

            // Background white
            drawRect(Color.White, Offset.Zero, size)

            // Standard corner finder squares
            fun drawFinder(startX: Int, startY: Int) {
                // Outer 7x7
                drawRect(
                    Color.Black,
                    Offset(startX * cellW, startY * cellH),
                    Size(7 * cellW, 7 * cellH)
                )
                // Inner 5x5 white
                drawRect(
                    Color.White,
                    Offset((startX + 1) * cellW, (startY + 1) * cellH),
                    Size(5 * cellW, 5 * cellH)
                )
                // Center 3x3 black
                drawRect(
                    Color.Black,
                    Offset((startX + 2) * cellW, (startY + 2) * cellH),
                    Size(3 * cellW, 3 * cellH)
                )
            }

            drawFinder(0, 0)
            drawFinder(14, 0)
            drawFinder(0, 14)

            // Timing patterns
            for (i in 7 until 14) {
                if (i % 2 == 0) {
                    drawRect(Color.Black, Offset(6 * cellW, i * cellH), Size(cellW, cellH))
                    drawRect(Color.Black, Offset(i * cellW, 6 * cellH), Size(cellW, cellH))
                }
            }

            // Fill data cells using hash
            for (y in 0 until sizeCount) {
                for (x in 0 until sizeCount) {
                    // Skip finder zones
                    val inTopLeft = x < 8 && y < 8
                    val inTopRight = x > 12 && y < 8
                    val inBottomLeft = x < 8 && y > 12
                    if (!inTopLeft && !inTopRight && !inBottomLeft) {
                        val bitIndex = (y * sizeCount + x) % (hash.size * 8)
                        val byteVal = hash[bitIndex / 8].toInt()
                        val bit = (byteVal shr (bitIndex % 8)) and 1
                        if (bit == 1) {
                            drawRect(
                                Color.Black,
                                Offset(x * cellW, y * cellH),
                                Size(cellW, cellH)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%d:%02d".format(m, s)
}
