package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EventEntity
import com.example.data.model.TrackEntity
import com.example.player.PlaybackState
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    playbackState: PlaybackState,
    allTracks: List<TrackEntity>,
    top10Tracks: List<TrackEntity>,
    recentTracks: List<TrackEntity>,
    dakarTracks: List<TrackEntity>,
    saintLouisTracks: List<TrackEntity>,
    events: List<EventEntity>,
    isProUser: Boolean = false,
    onPlayTrack: (TrackEntity) -> Unit,
    onDownloadTrack: (TrackEntity) -> Unit,
    onSelectEvent: (EventEntity) -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToPro: (() -> Unit)? = null
) {
    var selectedGenre by remember { mutableStateOf("Tous") }
    val genres = listOf("Tous", "Mbalax", "Rap Galsen", "Afrobeat", "Acoustic", "Amapiano")

    val filteredRecent = remember(recentTracks, selectedGenre) {
        if (selectedGenre == "Tous") recentTracks
        else recentTracks.filter { it.genre.equals(selectedGenre, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LightCanvasTop,
                        LightCanvasMid,
                        LightCanvasBottom
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_feed"),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner: Saint-Louis river / Pirogues de Guet Ndar & Official Logo
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(185.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.quay_hero_banner),
                            contentDescription = "Quai de Guet Ndar Saint-Louis",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, QuayGreenDark.copy(alpha = 0.92f))
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.quay_guett_logo),
                                    contentDescription = "Logo Officiel Quay Guett 221",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column {
                                Surface(
                                    color = QuayGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "QUAY GUETT 221 • GUET NDAR",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = QuayGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "La Musique Débarque. Le Live se Consomme.",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = SandWhite
                                )
                                Text(
                                    text = "Écoutez en illimité & réservez vos concerts en 1 clic Wave / OM.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SandWhite.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // PRO VERSION BANNER (Dégradé Or / Ambre)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToPro?.invoke() }
                        .testTag("home_pro_banner"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isProUser) ProGold else Color(0xFFFFD54F)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    if (isProUser) {
                                        listOf(Color(0xFFFFF9E6), Color(0xFFFFF2D6), Color(0xFFEBF5FB))
                                    } else {
                                        listOf(Color(0xFFFFF8E7), Color(0xFFFFF0CF), Color(0xFFE8F4FA))
                                    }
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(ProGold, ProAmber)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isProUser) Icons.Default.Verified else Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = OceanBlueDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isProUser) "QUAY PRO ACTIF" else "VERSION PRO 221",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = OceanBlue
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = ProGoldDark,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isProUser) "VIP" else "NOUVEAU",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isProUser)
                                            "Audio FLAC sans perte & Coupe-file VIP aux concerts actifs"
                                        else
                                            "FLAC 320k, Téléchargements illimités & Coupe-file VIP concerts",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = OceanBlue.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Button(
                                onClick = { onNavigateToPro?.invoke() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isProUser) OceanBlue else ProGoldDark
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (isProUser) "GÉRER" else "DÉCOUVRIR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

        // Genre Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OceanBlue,
                            selectedLabelColor = SandWhite,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // SECTION 1: NOUVEAUTÉS 221
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Nouveautés 221",
                    subtitle = "Les derniers sons débarqués sur le Quai",
                    actionText = "Voir tout"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredRecent.take(4).forEach { track ->
                        val isCurr = playbackState.currentTrack?.id == track.id
                        TrackRowItem(
                            track = track,
                            isPlaying = playbackState.isPlaying,
                            isCurrentTrack = isCurr,
                            hasLiveEvent = track.id in listOf("track_1", "track_2", "track_3"),
                            onPlayClick = { onPlayTrack(track) },
                            onDownloadClick = { onDownloadTrack(track) },
                            onLiveEventClick = {
                                val evt = events.firstOrNull()
                                if (evt != null) onSelectEvent(evt)
                            }
                        )
                    }
                }
            }
        }

        // FEATURED LIVE CONCERT HIGHLIGHT (Billetterie intégrée)
        if (events.isNotEmpty()) {
            val featuredEvent = events.first()
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        title = "À l'Affiche au Quai",
                        subtitle = "Billetterie instantanée sans quitter l'app",
                        actionText = "Tous les lives",
                        onActionClick = onNavigateToEvents
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectEvent(featuredEvent) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OceanBlueDark)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.event_guet_ndar),
                                    contentDescription = featuredEvent.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, OceanBlueDark)
                                            )
                                        )
                                )
                                Surface(
                                    color = CtaOrange,
                                    shape = RoundedCornerShape(bottomStart = 8.dp),
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "BILLETTERIE OUVERTE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = featuredEvent.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SandWhite
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = NetYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = featuredEvent.venueName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NetYellow
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = SandWhite.copy(alpha = 0.7f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = featuredEvent.dateTimeText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SandWhite.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Reste ${featuredEvent.totalCapacity - featuredEvent.soldCount} places",
                                        fontSize = 12.sp,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Button(
                                        onClick = { onSelectEvent(featuredEvent) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CtaOrange),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ConfirmationNumber,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Acheter (Wave/OM)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 2: TENDANCES DAKAR
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Tendances Dakar",
                    subtitle = "Les sons les plus streamés dans la capitale",
                    actionText = "Voir plus"
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(dakarTracks) { track ->
                        TrackCardCompact(
                            track = track,
                            isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                            onClick = { onPlayTrack(track) }
                        )
                    }
                }
            }
        }

        // SECTION 3: TENDANCES SAINT-LOUIS (Ndar Vibe!)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Tendances Saint-Louis (Ndar)",
                    subtitle = "L'énergie acoustique et live du Quai de Guet Ndar",
                    actionText = "Voir plus"
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(saintLouisTracks) { track ->
                        TrackCardCompact(
                            track = track,
                            isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                            onClick = { onPlayTrack(track) }
                        )
                    }
                }
            }
        }

        // SECTION 4: TOP 10 DE LA SEMAINE
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Top 10 de la Semaine",
                    subtitle = "Classement officiel QUAY GUET 221",
                    actionText = ""
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    top10Tracks.forEachIndexed { index, track ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "#${index + 1}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = if (index < 3) NetYellowDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.width(36.dp)
                            )
                            TrackRowItem(
                                track = track,
                                isPlaying = playbackState.isPlaying,
                                isCurrentTrack = playbackState.currentTrack?.id == track.id,
                                hasLiveEvent = track.id in listOf("track_1", "track_2", "track_3"),
                                onPlayClick = { onPlayTrack(track) },
                                onDownloadClick = { onDownloadTrack(track) },
                                onLiveEventClick = {
                                    val evt = events.firstOrNull()
                                    if (evt != null) onSelectEvent(evt)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
        if (actionText.isNotEmpty()) {
            TextButton(onClick = { onActionClick?.invoke() }) {
                Text(
                    text = actionText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TrackCardCompact(
    track: TrackEntity,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(OceanBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = NetYellow,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp
            )
        }
    }
}
