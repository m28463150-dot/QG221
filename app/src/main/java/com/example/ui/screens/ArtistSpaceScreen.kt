package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtistWalletEntity
import com.example.data.model.EventEntity
import com.example.data.model.TrackEntity
import com.example.ui.theme.*

@Composable
fun ArtistSpaceScreen(
    wallet: ArtistWalletEntity?,
    myTracks: List<TrackEntity>,
    myEvents: List<EventEntity>,
    onUploadTrack: (title: String, feat: String, genre: String, city: String, lyrics: String) -> Unit,
    onCreateEvent: (
        title: String,
        venue: String,
        city: String,
        dateTimeText: String,
        description: String,
        tickets: List<Pair<String, Pair<Int, Int>>>,
        linkedTrackIds: List<String>
    ) -> Unit,
    onRequestWithdrawal: (amountCfa: Int, provider: String, phone: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tableau de bord", "Upload Son", "Créer Événement", "Mon Wallet")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("artist_space_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = OceanBlueDark,
            contentColor = NetYellow
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Normal,
                            color = if (selectedTab == index) NetYellow else SandWhite.copy(alpha = 0.7f)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> ArtistDashboardTab(wallet = wallet, myTracks = myTracks, myEvents = myEvents)
            1 -> ArtistUploadTrackTab(onUploadTrack = onUploadTrack)
            2 -> ArtistCreateEventWizardTab(myTracks = myTracks, onCreateEvent = onCreateEvent)
            3 -> ArtistWalletTab(wallet = wallet, onRequestWithdrawal = onRequestWithdrawal)
        }
    }
}

@Composable
fun ArtistDashboardTab(
    wallet: ArtistWalletEntity?,
    myTracks: List<TrackEntity>,
    myEvents: List<EventEntity>
) {
    val totalPlays = remember(myTracks) { myTracks.sumOf { t: TrackEntity -> t.playsCount } }
    val totalTicketsSold = remember(myEvents) { myEvents.sumOf { e: EventEntity -> e.soldCount } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI Summary Cards
        item {
            Text(
                text = "Performance & Revenus",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = OceanBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Streams Totaux",
                    value = "$totalPlays",
                    icon = Icons.Default.GraphicEq,
                    accentColor = OceanBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Billets Vendus",
                    value = "$totalTicketsSold",
                    icon = Icons.Default.ConfirmationNumber,
                    accentColor = CtaOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "CA Brut (GMV)",
                    value = "${wallet?.totalGrossSalesCfa ?: 0} F",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Net Disponible",
                    value = "${wallet?.balanceCfa ?: 0} F",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = NetYellowDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Commission Breakdown Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Modèle Économique QUAY GUET 221",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Commission plateforme : 10% sur les tickets vendus (au succès)\n• Streaming : 100% gratuit et sans commission pour maximiser votre audience\n• Vos revenus nets sont virés directement sur Wave ou Orange Money.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Tracks Management List
        item {
            Text(
                text = "Vos Morceaux Débarqués au Quai (${myTracks.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                myTracks.forEach { track ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = track.title, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${track.genre} • ${track.city} • Encodé 128k & 320k",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${track.playsCount} streams",
                                    fontWeight = FontWeight.Black,
                                    color = OceanBlue,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (track.isApproved) "En Ligne" else "En Modération",
                                    fontSize = 10.sp,
                                    color = if (track.isApproved) SuccessGreen else NetYellowDark,
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

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = accentColor
            )
        }
    }
}

@Composable
fun ArtistUploadTrackTab(
    onUploadTrack: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var feat by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Mbalax") }
    var city by remember { mutableStateOf("Saint-Louis") }
    var lyrics by remember { mutableStateOf("") }

    val genres = listOf("Mbalax", "Rap Galsen", "Afrobeat", "Acoustic", "Amapiano", "Sabar Beat")
    val cities = listOf("Saint-Louis", "Rufisque", "Dakar", "Thiès", "Ziguinchor")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Débarquer un Nouveau Morceau",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = OceanBlue
            )
            Text(
                text = "Transcodage automatique en 128 kbps (économique) & 320 kbps (haute qualité)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du morceau *") },
                placeholder = { Text("ex: Thiéboudienne Vibe") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = feat,
                onValueChange = { feat = it },
                label = { Text("Featuring (Optionnel)") },
                placeholder = { Text("ex: Dip Doundou Guiss") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("Genre musical :", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                genres.take(3).forEach { g ->
                    FilterChip(
                        selected = genre == g,
                        onClick = { genre = g },
                        label = { Text(g, fontSize = 11.sp) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                genres.drop(3).forEach { g ->
                    FilterChip(
                        selected = genre == g,
                        onClick = { genre = g },
                        label = { Text(g, fontSize = 11.sp) }
                    )
                }
            }
        }

        item {
            Text("Ville d'ancrage (Scène locale) :", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                cities.take(3).forEach { c ->
                    FilterChip(
                        selected = city == c,
                        onClick = { city = c },
                        label = { Text(c, fontSize = 11.sp) }
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = lyrics,
                onValueChange = { lyrics = it },
                label = { Text("Paroles (Wolof / Français)") },
                placeholder = { Text("Jekk naa ci Quay bi...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onUploadTrack(title.trim(), feat.trim(), genre, city, lyrics)
                        title = ""
                        feat = ""
                        lyrics = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("upload_track_submit_btn")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publier le morceau sur le Quai", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =============================================================================
// WIZARD CRÉATION ÉVÉNEMENT EN 4 ÉTAPES AVEC LIAISON MUSICALE (CAHIER DES CHARGES B1)
// =============================================================================
@Composable
fun ArtistCreateEventWizardTab(
    myTracks: List<TrackEntity>,
    onCreateEvent: (
        title: String,
        venue: String,
        city: String,
        dateTimeText: String,
        description: String,
        tickets: List<Pair<String, Pair<Int, Int>>>,
        linkedTrackIds: List<String>
    ) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Step 1: Info
    var title by remember { mutableStateOf("Concert Acoustique au Quai") }
    var venue by remember { mutableStateOf("Quai de Guet Ndar, Saint-Louis") }
    var city by remember { mutableStateOf("Saint-Louis") }
    var dateTimeText by remember { mutableStateOf("Samedi 24 Octobre 2026 • 20h30") }
    var description by remember { mutableStateOf("Une soirée live mémorable face aux pirogues de Guet Ndar. Énergie acoustique et mbalax pur.") }

    // Step 2: Tickets
    var standardPrice by remember { mutableStateOf("3000") }
    var standardQty by remember { mutableStateOf("150") }
    var vipPrice by remember { mutableStateOf("8000") }
    var vipQty by remember { mutableStateOf("30") }

    // Step 3: Liaison Musicale (Morceaux liés)
    var selectedLinkedTrackIds by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Step Indicator (1 to 4)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Créer un Événement Live",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = OceanBlue
                )
                Surface(
                    color = NetYellow,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Étape $step / 4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanBlueDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        when (step) {
            1 -> {
                item {
                    Text("Étape 1 : Informations Générales", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'événement *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Lieu / Adresse *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = dateTimeText,
                        onValueChange = { dateTimeText = it },
                        label = { Text("Date et Heure du Live *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Suivant : Configurer les Billets")
                    }
                }
            }

            2 -> {
                item {
                    Text("Étape 2 : Types de Billets & Jauges", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Définissez les catégories et prix en FCFA",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Billet Standard", fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = standardPrice,
                                    onValueChange = { standardPrice = it },
                                    label = { Text("Prix (FCFA)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = standardQty,
                                    onValueChange = { standardQty = it },
                                    label = { Text("Quantité") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Billet VIP", fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = vipPrice,
                                    onValueChange = { vipPrice = it },
                                    label = { Text("Prix (FCFA)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = vipQty,
                                    onValueChange = { vipQty = it },
                                    label = { Text("Quantité") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { step = 1 }, modifier = Modifier.weight(1f)) {
                            Text("Précédent")
                        }
                        Button(
                            onClick = { step = 3 },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Suivant : Liaison")
                        }
                    }
                }
            }

            3 -> {
                // LIAISON MUSICALE CRITIQUE (B1)
                item {
                    Surface(
                        color = NetYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Liaison Musicale (Innovation Quay Guet)",
                                fontWeight = FontWeight.Black,
                                color = OceanBlueDark
                            )
                            Text(
                                text = "Cochez les morceaux que vous chanterez à ce concert. Dès qu'un auditeur écoutera l'un de ces sons, un bouton 'VOIR EN LIVE' clignotera dans son lecteur pour lui vendre ce billet !",
                                fontSize = 11.sp,
                                color = OceanBlueDark
                            )
                        }
                    }
                }

                item {
                    Text("Sélectionnez vos morceaux à lier :", fontWeight = FontWeight.Bold)
                }

                items(myTracks) { track ->
                    val isChecked = track.id in selectedLinkedTrackIds
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedLinkedTrackIds = if (isChecked) {
                                    selectedLinkedTrackIds - track.id
                                } else {
                                    selectedLinkedTrackIds + track.id
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) OceanBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, fontWeight = FontWeight.Bold)
                                Text("${track.genre} • ${track.playsCount} streams", fontSize = 11.sp)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedLinkedTrackIds = if (checked) {
                                        selectedLinkedTrackIds + track.id
                                    } else {
                                        selectedLinkedTrackIds - track.id
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { step = 2 }, modifier = Modifier.weight(1f)) {
                            Text("Précédent")
                        }
                        Button(
                            onClick = { step = 4 },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Suivant : Récap")
                        }
                    }
                }
            }

            4 -> {
                // RÉCAPITULATIF & MISE EN VENTE
                item {
                    Text("Étape 4 : Récapitulatif & Mise en Vente", fontWeight = FontWeight.Bold)
                }

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                            Text("📍 $venue ($city)", fontSize = 12.sp)
                            Text("📅 $dateTimeText", fontSize = 12.sp)
                            Divider()
                            Text("Billets configurés :", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("• Standard : $standardPrice FCFA ($standardQty places)", fontSize = 11.sp)
                            Text("• VIP : $vipPrice FCFA ($vipQty places)", fontSize = 11.sp)
                            Divider()
                            Text("Morceaux liés : ${selectedLinkedTrackIds.size} morceau(x) activé(s) pour la liaison musicale live.", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            val ticketsList = listOf(
                                "Standard" to Pair(standardPrice.toIntOrNull() ?: 3000, standardQty.toIntOrNull() ?: 100),
                                "VIP" to Pair(vipPrice.toIntOrNull() ?: 8000, vipQty.toIntOrNull() ?: 20)
                            )
                            onCreateEvent(
                                title,
                                venue,
                                city,
                                dateTimeText,
                                description,
                                ticketsList,
                                selectedLinkedTrackIds.toList()
                            )
                            step = 1
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CtaOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_event_submit_btn")
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publier l'événement au Quai !", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistWalletTab(
    wallet: ArtistWalletEntity?,
    onRequestWithdrawal: (Int, String, String) -> Unit
) {
    var withdrawalAmount by remember { mutableStateOf("15000") }
    var withdrawalPhone by remember { mutableStateOf("+221 78 456 78 90") }
    var selectedMethod by remember { mutableStateOf("Wave") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OceanBlueDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "SOLDE DISPONIBLE AU RETRAIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SandWhite.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${wallet?.balanceCfa ?: 0} FCFA",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = NetYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Revenus nets après prélèvement de la commission de 10% de la plateforme.",
                        fontSize = 11.sp,
                        color = SandWhite.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Demande de Retrait Mobile Money",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Minimum légal de retrait : 10 000 FCFA. Virement sous 24h ouvrées.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        item {
            OutlinedTextField(
                value = withdrawalAmount,
                onValueChange = { withdrawalAmount = it },
                label = { Text("Montant à retirer (FCFA) *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedMethod == "Wave",
                    onClick = { selectedMethod = "Wave" },
                    label = { Text("Wave (+221)") }
                )
                FilterChip(
                    selected = selectedMethod == "Orange Money",
                    onClick = { selectedMethod = "Orange Money" },
                    label = { Text("Orange Money") }
                )
            }
        }

        item {
            OutlinedTextField(
                value = withdrawalPhone,
                onValueChange = { withdrawalPhone = it },
                label = { Text("Numéro mobile money bénéficiaire") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    val amount = withdrawalAmount.toIntOrNull() ?: 0
                    if (amount >= 10000) {
                        onRequestWithdrawal(amount, selectedMethod, withdrawalPhone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("request_withdrawal_submit_btn")
            ) {
                Icon(Icons.Default.Payment, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Demander le virement", fontWeight = FontWeight.Bold)
            }
        }
    }
}
