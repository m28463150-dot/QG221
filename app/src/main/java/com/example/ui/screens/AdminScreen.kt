package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.TrackEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    users: List<UserEntity>,
    pendingTracks: List<TrackEntity>,
    pendingEvents: List<EventEntity>,
    transactions: List<TransactionEntity>,
    onModerateTrack: (String, Boolean) -> Unit,
    onModerateEvent: (String, Boolean) -> Unit,
    onToggleUserVerified: (String, Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Supervision", "Modération (${pendingTracks.size + pendingEvents.size})", "Artistes Vérifiés")

    val totalGMV = remember(transactions) {
        transactions.filter { it.type == "ticket_purchase" }.sumOf { tx: TransactionEntity -> tx.amountCfa }
    }
    val platformCommissions = remember(transactions) {
        transactions.sumOf { tx: TransactionEntity -> tx.commissionCfa }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NightBackground,
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
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NetYellow else SandWhite.copy(alpha = 0.7f)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // SUPERVISION FINANCIÈRE (Module D2)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Supervision Financière QUAY GUET 221",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = OceanBlue
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "Volume Ventes (GMV)",
                                value = "$totalGMV F",
                                icon = Icons.Default.Payments,
                                accentColor = OceanBlue,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Commissions Quai (10%)",
                                value = "$platformCommissions F",
                                icon = Icons.Default.Savings,
                                accentColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Journal des Transactions Temps Réel",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(transactions) { tx ->
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
                                    Text(
                                        text = "${tx.type.uppercase()} • ${tx.paymentProvider}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Client: ${tx.buyerPhone} • Date: ${tx.createdAtText.take(16)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    if (tx.commissionCfa > 0) {
                                        Text(
                                            text = "Commission Quai perçue : +${tx.commissionCfa} FCFA",
                                            fontSize = 10.sp,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "${tx.amountCfa} FCFA",
                                    fontWeight = FontWeight.Black,
                                    color = if (tx.type == "ticket_purchase") CtaOrange else ErrorRed
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // MODÉRATION & CONTENU (Module D1)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "File de Modération des Nouveaux Contenus",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    if (pendingTracks.isEmpty() && pendingEvents.isEmpty()) {
                        item {
                            Text(
                                text = "Aucun contenu en attente de modération. Tout est à jour !",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (pendingTracks.isNotEmpty()) {
                        item {
                            Text("Morceaux en attente (${pendingTracks.size}) :", fontWeight = FontWeight.Bold)
                        }
                        items(pendingTracks) { track ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(track.title, fontWeight = FontWeight.Bold)
                                        Text("Par @${track.artistName} • ${track.genre}", fontSize = 11.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(onClick = { onModerateTrack(track.id, false) }) {
                                            Icon(Icons.Default.Close, contentDescription = "Rejeter", tint = ErrorRed)
                                        }
                                        IconButton(onClick = { onModerateTrack(track.id, true) }) {
                                            Icon(Icons.Default.Check, contentDescription = "Valider", tint = SuccessGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pendingEvents.isNotEmpty()) {
                        item {
                            Text("Concerts en attente (${pendingEvents.size}) :", fontWeight = FontWeight.Bold)
                        }
                        items(pendingEvents) { evt ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(evt.title, fontWeight = FontWeight.Bold)
                                        Text("${evt.venueName} • ${evt.dateTimeText}", fontSize = 11.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(onClick = { onModerateEvent(evt.id, false) }) {
                                            Icon(Icons.Default.Close, contentDescription = "Rejeter", tint = ErrorRed)
                                        }
                                        IconButton(onClick = { onModerateEvent(evt.id, true) }) {
                                            Icon(Icons.Default.Check, contentDescription = "Valider", tint = SuccessGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // GESTION DES ARTISTES ET BADGES VÉRIFIÉS (Module D3)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Attribution des Badges Artistes Vérifiés",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            text = "Permet aux créateurs de mettre en vente des billets de concert.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    items(users) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("@${user.username}", fontWeight = FontWeight.Bold)
                                        if (user.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = OceanBlue, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text("Rôle : ${user.role} • ${user.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }

                                Switch(
                                    checked = user.isVerified,
                                    onCheckedChange = { checked ->
                                        onToggleUserVerified(user.id, checked)
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
