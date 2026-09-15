package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PlaylistEntity
import com.example.data.model.TicketEntity
import com.example.data.model.TrackEntity
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.PlaylistCardItem
import com.example.ui.components.QrCodeView
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.*
import com.example.util.ShareHelper
import com.example.util.TicketExportHelper

@Composable
fun LibraryScreen(
    tickets: List<TicketEntity>,
    downloadedTracks: List<TrackEntity>,
    favoriteTracks: List<TrackEntity>,
    playlists: List<PlaylistEntity> = emptyList(),
    onPlayTrack: (TrackEntity) -> Unit,
    onDeleteDownload: (TrackEntity) -> Unit,
    onPurgeAllDownloads: () -> Unit,
    onTransferTicket: (String, String) -> Unit,
    onNavigateToEvents: () -> Unit,
    onSelectPlaylist: (PlaylistEntity) -> Unit = {},
    onCreatePlaylist: (String, String) -> Unit = { _, _ -> },
    onPlayPlaylist: (PlaylistEntity) -> Unit = {},
    onDeletePlaylist: (String) -> Unit = {},
    onAddToPlaylistClick: ((TrackEntity) -> Unit)? = null,
    onArtistBioClick: ((String) -> Unit)? = null,
    onShareTrackClick: ((TrackEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Mes Tickets", "Playlists", "Téléchargements", "Favoris")

    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    // State for QR zoom and transfer
    var zoomedTicket by remember { mutableStateOf<TicketEntity?>(null) }
    var transferTicketTarget by remember { mutableStateOf<TicketEntity?>(null) }
    var transferPhoneInput by remember { mutableStateOf("+221 ") }

    Column(
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
            .testTag("library_screen")
    ) {
        // Tab selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = OceanBlue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // TAB 1: MES TICKETS (Module B4)
                if (tickets.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aucun Billet pour l'instant",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Achetez vos tickets de concert en direct via Wave ou Orange Money.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToEvents,
                            colors = ButtonDefaults.buttonColors(containerColor = CtaOrange)
                        ) {
                            Text("Découvrir les concerts à l'affiche")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                color = NetYellow.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = OceanBlueDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Touchez un QR code pour l'agrandir au contrôle d'accès. Vos billets sont valides hors-ligne !",
                                        fontSize = 12.sp,
                                        color = OceanBlueDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        items(tickets) { ticket ->
                            TicketCardItem(
                                ticket = ticket,
                                onZoomQr = { zoomedTicket = ticket },
                                onTransferClick = {
                                    transferTicketTarget = ticket
                                    transferPhoneInput = "+221 "
                                },
                                onSaveToGallery = {
                                    TicketExportHelper.generateAndSaveTicketImage(context, ticket)
                                },
                                onShareWhatsApp = {
                                    ShareHelper.shareTicket(context, ticket, toWhatsApp = true)
                                }
                            )
                        }
                    }
                }
            }

            1 -> {
                // TAB 2: PLAYLISTS PERSONNALISÉES
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mes Listes de Lecture",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${playlists.size} playlist(s) personnalisée(s)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = { showCreatePlaylistDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Créer", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suggestions rapides pour l'utilisateur
                    Text(
                        text = "IDÉES POPULAIRES :",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanBlue,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val suggestions = listOf(
                            "Ndawrabine Vibes" to "Rythmes traditionnels Lebou & pêcheurs de Guet Ndar",
                            "Saint-Louis Jazz" to "Ambiance feutrée de l'île et de l'Institut",
                            "Soirée Guet Ndar" to "L'ambiance festive des quais nocturnes",
                            "Acoustic Kora" to "Cordes douces et mélodies fluviales"
                        )
                        items(suggestions) { (title, desc) ->
                            SuggestionChip(
                                onClick = { onCreatePlaylist(title, desc) },
                                label = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                icon = {
                                    Icon(
                                        Icons.Default.PlaylistAdd,
                                        contentDescription = null,
                                        tint = NetYellowDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (playlists.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = OceanBlue.copy(alpha = 0.4f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = "Aucune playlist pour l'instant",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Créez votre première liste de lecture (ex: « Ndawrabine Vibes ») pour rassembler vos morceaux !",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { showCreatePlaylistDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = OceanBlue)
                                ) {
                                    Text("Créer une playlist")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(playlists) { pl ->
                                PlaylistCardItem(
                                    playlist = pl,
                                    onClick = { onSelectPlaylist(pl) },
                                    onPlayClick = { onPlayPlaylist(pl) },
                                    onDeleteClick = { onDeletePlaylist(pl.id) }
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 3: TÉLÉCHARGEMENTS (Module A4 Offline First)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Gestionnaire Hors-Ligne",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${downloadedTracks.size} son(s) chiffré(s) dans le sandbox",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        if (downloadedTracks.isNotEmpty()) {
                            TextButton(
                                onClick = onPurgeAllDownloads,
                                colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tout purger", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (downloadedTracks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucun morceau téléchargé.\nTéléchargez vos sons en WiFi pour les écouter sans data !",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(downloadedTracks) { track ->
                                TrackRowItem(
                                    track = track,
                                    isPlaying = false,
                                    isCurrentTrack = false,
                                    onPlayClick = { onPlayTrack(track) },
                                    onDownloadClick = { onDeleteDownload(track) },
                                    onAddToPlaylistClick = onAddToPlaylistClick?.let { { it(track) } },
                                    onArtistBioClick = onArtistBioClick?.let { { it(track.artistId) } },
                                    onShareClick = onShareTrackClick?.let { { it(track) } }
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                // TAB 4: FAVORIS
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteTracks) { track ->
                        TrackRowItem(
                            track = track,
                            isPlaying = false,
                            isCurrentTrack = false,
                            onPlayClick = { onPlayTrack(track) },
                            onDownloadClick = { },
                            onAddToPlaylistClick = onAddToPlaylistClick?.let { { it(track) } },
                            onArtistBioClick = onArtistBioClick?.let { { it(track.artistId) } },
                            onShareClick = onShareTrackClick?.let { { it(track) } }
                        )
                    }
                }
            }
        }
    }

    // ZOOM QR CODE MODAL
    if (zoomedTicket != null) {
        val t = zoomedTicket!!
        Dialog(onDismissRequest = { zoomedTicket = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "PRÉSENTER AU VIGILE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanBlue,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = t.eventTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Surface(
                        color = NetYellow,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = t.ticketTypeName.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OceanBlueDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // High Contrast Large QR
                    QrCodeView(
                        data = t.id,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        text = "ID: ${t.id}",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Statut : ${t.status.uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (t.status == "valid") SuccessGreen else ErrorRed
                    )

                    // Export physique et partage WhatsApp
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                TicketExportHelper.generateAndSaveTicketImage(context, t)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sauvegarder dans la Galerie Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    ShareHelper.shareTicket(context, t, toWhatsApp = true)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = SuccessGreen.copy(alpha = 0.2f),
                                    contentColor = SuccessGreen
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = {
                                    TicketExportHelper.shareTicketImage(context, t)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = NetYellow.copy(alpha = 0.25f),
                                    contentColor = OceanBlueDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Partager Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { zoomedTicket = null },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fermer le plein écran")
                    }
                }
            }
        }
    }

    // CREATE PLAYLIST MODAL
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { title, desc ->
                showCreatePlaylistDialog = false
                onCreatePlaylist(title, desc)
            }
        )
    }

    // TRANSFER TICKET MODAL
    if (transferTicketTarget != null) {
        val target = transferTicketTarget!!
        AlertDialog(
            onDismissRequest = { transferTicketTarget = null },
            title = { Text("Transférer ce billet") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Vous êtes sur le point de transférer votre place '${target.ticketTypeName}' pour '${target.eventTitle}'.",
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = transferPhoneInput,
                        onValueChange = { transferPhoneInput = it },
                        label = { Text("Numéro du destinataire") },
                        placeholder = { Text("+221 77 000 00 00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Le billet sera réassigné au compte du nouveau propriétaire.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (transferPhoneInput.length > 8) {
                            onTransferTicket(target.id, transferPhoneInput.trim())
                            transferTicketTarget = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CtaOrange)
                ) {
                    Text("Confirmer le transfert")
                }
            },
            dismissButton = {
                TextButton(onClick = { transferTicketTarget = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun TicketCardItem(
    ticket: TicketEntity,
    onZoomQr: () -> Unit,
    onTransferClick: () -> Unit,
    onSaveToGallery: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ticket.status == "scanned") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (ticket.status == "valid") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (ticket.status == "valid") "BILLET VALIDE" else "BILLET UTILISÉ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (ticket.status == "valid") SuccessGreen else ErrorRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = OceanBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = ticket.ticketTypeName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticket.eventTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ticket.eventDateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = ticket.venueName,
                        style = MaterialTheme.typography.bodySmall,
                        color = NetYellowDark,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "ID: ${ticket.id}",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                // Tappable QR Code
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { onZoomQr() }
                        .padding(4.dp)
                ) {
                    QrCodeView(
                        data = ticket.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = onZoomQr,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("QR", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = onSaveToGallery,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = OceanBlue.copy(alpha = 0.12f),
                            contentColor = OceanBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onShareWhatsApp,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SuccessGreen.copy(alpha = 0.15f),
                            contentColor = SuccessGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (ticket.status == "valid") {
                    OutlinedButton(
                        onClick = onTransferClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Transférer", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
