package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TicketEntity
import com.example.data.model.UserEntity
import com.example.player.PlaybackState
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserEntity,
    playbackState: PlaybackState,
    userTickets: List<TicketEntity>,
    downloadedTracksCount: Int,
    isProUser: Boolean = false,
    onToggleOfflineMode: () -> Unit,
    onChangeQuality: (String) -> Unit,
    onSwitchUserRole: (String) -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToArtistSpace: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToPro: () -> Unit = {},
    onTogglePro: () -> Unit = {}
) {
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
                .testTag("profile_screen"),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = OceanBlueDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (isProUser) listOf(ProGold, ProAmber, CtaOrange)
                                        else listOf(NetYellow, CtaOrange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.username.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = OceanBlueDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "@" + currentUser.username,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = SandWhite
                            )
                            if (currentUser.isVerified || currentUser.role == "artist") {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Vérifié",
                                    tint = NetYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (isProUser) {
                                Surface(
                                    color = ProGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = OceanBlueDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = currentUser.phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SandWhite.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = if (isProUser) ProGold.copy(alpha = 0.25f) else NetYellow.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (isProUser) "MEMBRE QUAY PRO ⭐ ACCÈS VIP CONCERTS" else when (currentUser.role) {
                                    "artist" -> "COMPTE ARTISTE OFFICIEL"
                                    "vigil" -> "COMPTE CONTRÔLE VIGILE"
                                    "admin" -> "COMPTE ADMINISTRATEUR"
                                    else -> "AUDITEUR & PASSIONNÉ DU LIVE"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isProUser) ProGold else NetYellow,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // QUAY PRO SUBSCRIPTION CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPro() }
                        .testTag("profile_pro_card"),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(ProGold, ProAmber))
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    if (isProUser) {
                                        listOf(Color(0xFFFFF9E8), Color(0xFFFFF2D6), Color(0xFFF0F7FB))
                                    } else {
                                        listOf(Color(0xFFFFFDF5), Color(0xFFFFF3D9), Color(0xFFEBF4FA))
                                    }
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(listOf(ProGold, ProAmber))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = OceanBlueDark,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isProUser) "Abonnement QUAY PRO Actif ⭐" else "Version PRO • Expérience Ultime",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = OceanBlue
                                        )
                                        Text(
                                            text = if (isProUser) "FLAC Sans Perte • Coupe-file VIP aux concerts" else "2 000 FCFA/mois ou 19 000 FCFA/an",
                                            fontSize = 11.sp,
                                            color = ProGoldDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onNavigateToPro() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isProUser) OceanBlue else ProGoldDark
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isProUser) "Basculer rapidement en mode standard (test) :" else "Activer instantanément pour tester (1-clic) :",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Switch(
                                    checked = isProUser,
                                    onCheckedChange = { onTogglePro() },
                                    modifier = Modifier.height(26.dp)
                                )
                            }
                        }
                    }
                }
            }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatPill(
                    count = "${userTickets.size}",
                    label = "Billets Actifs",
                    icon = Icons.Default.ConfirmationNumber,
                    onClick = onNavigateToLibrary,
                    modifier = Modifier.weight(1f)
                )

                ProfileStatPill(
                    count = "$downloadedTracksCount",
                    label = "Sons Offline",
                    icon = Icons.Default.DownloadDone,
                    onClick = onNavigateToLibrary,
                    modifier = Modifier.weight(1f)
                )

                ProfileStatPill(
                    count = if (currentUser.role == "artist") "6" else "38",
                    label = if (currentUser.role == "artist") "Titres au Quai" else "Favoris",
                    icon = Icons.Default.MusicNote,
                    onClick = onNavigateToLibrary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Preferences: Audio Quality & Offline Mode
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Économie de Données & Streaming",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Offline mode switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (playbackState.isOfflineModeActive) Icons.Default.AirplanemodeActive else Icons.Default.Wifi,
                                contentDescription = null,
                                tint = if (playbackState.isOfflineModeActive) CtaOrange else SuccessGreen
                            )
                            Column {
                                Text(
                                    text = "Mode Hors-Ligne Forcé",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Bloque tout usage réseau mobile et ne lit que vos téléchargements",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Switch(
                            checked = playbackState.isOfflineModeActive,
                            onCheckedChange = { onToggleOfflineMode() }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Quality Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Qualité Audio de Lecture",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = if (playbackState.quality == "LOW") "128 kbps (Économie maximale de data)" else "320 kbps (Son studio haute clarté)",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = playbackState.quality == "LOW",
                                onClick = { onChangeQuality("LOW") },
                                label = { Text("128k", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = playbackState.quality == "HIGH",
                                onClick = { onChangeQuality("HIGH") },
                                label = { Text("320k", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Services & Portails
        item {
            Text(
                text = "Services de la Plateforme",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        title = "Billetterie & Concerts Live",
                        subtitle = "Achetez vos places de concerts au Sénégal",
                        icon = Icons.Default.ConfirmationNumber,
                        iconTint = CtaOrange,
                        onClick = onNavigateToEvents
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileMenuRow(
                        title = "Espace Artiste & Créateur",
                        subtitle = "Upload de sons, programmation live et wallet",
                        icon = Icons.Default.Mic,
                        iconTint = OceanBlue,
                        onClick = onNavigateToArtistSpace
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileMenuRow(
                        title = "Mode Scanner Vigile",
                        subtitle = "Contrôle d'accès aux portes d'entrée avec cache offline",
                        icon = Icons.Default.QrCodeScanner,
                        iconTint = SuccessGreen,
                        onClick = onNavigateToScanner
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileMenuRow(
                        title = "Supervision & Administration",
                        subtitle = "Back-Office modération et commissions de 10%",
                        icon = Icons.Default.Shield,
                        iconTint = NetYellowDark,
                        onClick = onNavigateToAdmin
                    )
                }
            }
        }

        // Switch Role Quick Buttons
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tester un Autre Profil Utilisateur :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoleSwitchChip(
                            label = "Modou (Fan)",
                            isSelected = currentUser.role == "listener",
                            onClick = { onSwitchUserRole("listener") },
                            modifier = Modifier.weight(1f)
                        )
                        RoleSwitchChip(
                            label = "Awa (Artiste)",
                            isSelected = currentUser.role == "artist",
                            onClick = { onSwitchUserRole("artist") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoleSwitchChip(
                            label = "Vigile (Entrée)",
                            isSelected = currentUser.role == "vigil",
                            onClick = { onSwitchUserRole("vigil") },
                            modifier = Modifier.weight(1f)
                        )
                        RoleSwitchChip(
                            label = "Admin (Plateforme)",
                            isSelected = currentUser.role == "admin",
                            onClick = { onSwitchUserRole("admin") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // App Information & Official Logo Heritage Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
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
                        Text(
                            text = "QUAY GUETT 221",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = QuayGreen
                        )
                        Text(
                            text = "L'Arbre Musical & l'Esprit du Quai",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = QuayGoldDark
                        )
                        Text(
                            text = "Saint-Louis, Guet Ndar • Sénégal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "« Jekk naa ci Quay bi »",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuayRed
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun ProfileStatPill(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = OceanBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = OceanBlue
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RoleSwitchChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) OceanBlue else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(34.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SandWhite else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
