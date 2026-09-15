package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChartRankItem
import com.example.data.model.TrackEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopChartsScreen(
    charts: List<ChartRankItem>,
    selectedPeriod: String,
    onPeriodSelect: (String) -> Unit,
    onTrackClick: (TrackEntity) -> Unit,
    onAddToPlaylist: (TrackEntity) -> Unit,
    onOpenComments: (TrackEntity) -> Unit,
    onTipArtist: (TrackEntity) -> Unit,
    onShareTrack: (TrackEntity) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            tint = NetYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Top Charts 221",
                            fontWeight = FontWeight.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanBlueDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("top_charts_scroll"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Filter Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val periods = listOf(
                        "top20" to "Top 20 Sénégal",
                        "24h" to "Tendance 24h 🔥",
                        "saint_louis" to "Saint-Louis & Fleuve 🌊"
                    )
                    periods.forEach { (key, label) ->
                        val isSelected = selectedPeriod == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPeriodSelect(key) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OceanBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Top 3 Podium
            if (charts.size >= 3) {
                item {
                    val top1 = charts.getOrNull(0)
                    val top2 = charts.getOrNull(1)
                    val top3 = charts.getOrNull(2)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LE PODIUM DE LA SEMAINE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NetYellow,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // 2nd Place
                                if (top2 != null) {
                                    PodiumPillar(
                                        item = top2,
                                        rank = 2,
                                        medalColor = Color(0xFFC0C0C0), // Silver
                                        pillarHeight = 110.dp,
                                        onPlay = { onTrackClick(top2.track) }
                                    )
                                }

                                // 1st Place (Center & Taller)
                                if (top1 != null) {
                                    PodiumPillar(
                                        item = top1,
                                        rank = 1,
                                        medalColor = NetYellow, // Gold
                                        pillarHeight = 140.dp,
                                        onPlay = { onTrackClick(top1.track) }
                                    )
                                }

                                // 3rd Place
                                if (top3 != null) {
                                    PodiumPillar(
                                        item = top3,
                                        rank = 3,
                                        medalColor = Color(0xFFCD7F32), // Bronze
                                        pillarHeight = 90.dp,
                                        onPlay = { onTrackClick(top3.track) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Divider Title for rest of charts
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Classement Général (#4 à #${charts.size.coerceAtLeast(4)})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Rest of Tracks (4 to end)
            val rest = charts.drop(3)
            items(rest, key = { it.track.id }) { item ->
                ChartRankRow(
                    item = item,
                    onPlay = { onTrackClick(item.track) },
                    onAddToPlaylist = { onAddToPlaylist(item.track) },
                    onOpenComments = { onOpenComments(item.track) },
                    onTip = { onTipArtist(item.track) },
                    onShare = { onShareTrack(item.track) }
                )
            }
        }
    }
}

@Composable
private fun PodiumPillar(
    item: ChartRankItem,
    rank: Int,
    medalColor: Color,
    pillarHeight: androidx.compose.ui.unit.Dp,
    onPlay: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        // Track Artwork / Play button
        Box(
            modifier = Modifier
                .size(if (rank == 1) 68.dp else 56.dp)
                .clip(CircleShape)
                .border(2.dp, medalColor, CircleShape)
                .clickable { onPlay() },
            contentAlignment = Alignment.Center
        ) {
            val coverRes = if (item.track.artistName.contains("Awa", ignoreCase = true)) {
                R.drawable.quay_hero_banner
            } else if (item.track.artistName.contains("Ngaaka", ignoreCase = true)) {
                R.drawable.event_guet_ndar
            } else {
                R.drawable.quay_guett_logo
            }

            Image(
                painter = painterResource(id = coverRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Play overlay icon
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.track.title,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = item.track.artistName,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pillar block
        Surface(
            color = medalColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = if (rank == 1) 22.sp else 18.sp,
                    color = medalColor
                )
                Text(
                    text = "${item.weeklyStreams} streams",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ChartRankRow(
    item: ChartRankItem,
    onPlay: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onOpenComments: () -> Unit,
    onTip: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number & Trend
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
            ) {
                Text(
                    text = "${item.rank}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                when (item.trend) {
                    "UP" -> Text(text = "▲", color = SuccessGreen, fontSize = 10.sp)
                    "DOWN" -> Text(text = "▼", color = Color.Red, fontSize = 10.sp)
                    "NEW" -> Text(text = "NEW", color = OceanBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    else -> Text(text = "—", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cover
            val coverRes = if (item.track.artistName.contains("Awa", ignoreCase = true)) {
                R.drawable.quay_hero_banner
            } else if (item.track.artistName.contains("Ngaaka", ignoreCase = true)) {
                R.drawable.event_guet_ndar
            } else {
                R.drawable.quay_guett_logo
            }
            Image(
                painter = painterResource(id = coverRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.track.artistName} • ${item.weeklyStreams} écoutes",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenComments) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Commentaires",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onTip) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = "Pourboire",
                        tint = NetYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partager",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
