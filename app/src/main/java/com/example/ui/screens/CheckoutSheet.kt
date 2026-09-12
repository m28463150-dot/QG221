package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.EventEntity
import com.example.data.model.TicketTypeEntity
import com.example.ui.components.QrCodeView
import com.example.ui.theme.*
import com.example.viewmodel.PaymentStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
    event: EventEntity,
    selectedQuantities: Map<TicketTypeEntity, Int>,
    selectedProvider: String,
    paymentState: PaymentStep,
    buyerPhone: String,
    onSelectProvider: (String) -> Unit,
    onConfirmPurchase: () -> Unit,
    onDismiss: () -> Unit,
    onViewMyTickets: () -> Unit
) {
    val totalAmount = remember(selectedQuantities) {
        selectedQuantities.entries.sumOf { (type, qty) -> type.priceCfa * qty }
    }
    val totalTickets = remember(selectedQuantities) {
        selectedQuantities.values.sum()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (paymentState) {
                is PaymentStep.PROCESSING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (selectedProvider == "Wave") WaveCyan else OrangeMoney,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Validation PayDunya Sénégal...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Un push de paiement a été envoyé sur votre compte $selectedProvider ($buyerPhone).\nApprobation en cours...",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                is PaymentStep.SUCCESS -> {
                    val tickets = paymentState.tickets
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Paiement réussi",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Paiement Réussi !",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = OceanBlue
                        )

                        Text(
                            text = "Félicitations ! Vous avez vos entrées pour '${event.title}'. Vos billets QR codes sont disponibles hors-connexion dans votre bibliothèque.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Sample QR preview of the first generated ticket
                        if (tickets.isNotEmpty()) {
                            val first = tickets.first()
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    QrCodeView(
                                        data = first.id,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Billet #${first.id}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Catégorie : ${first.ticketTypeName}",
                                            fontSize = 11.sp,
                                            color = CtaOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Statut : VALIDE",
                                            fontSize = 10.sp,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onViewMyTickets()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("view_my_tickets_btn")
                        ) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voir mes billets dans la Bibliothèque", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    // SELECTION & CONFIRMATION
                    Text(
                        text = "Règlement Sécurisé PayDunya",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = OceanBlue
                    )
                    Text(
                        text = "Agrégateur agréé Banque Centrale (Sénégal)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recap Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = event.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${event.venueName} • ${event.dateTimeText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))

                            selectedQuantities.filterValues { it > 0 }.forEach { (type, qty) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "$qty x ${type.name}", fontSize = 12.sp)
                                    Text(text = "${type.priceCfa * qty} FCFA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total à régler",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "$totalAmount FCFA",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = CtaOrange
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sélectionnez votre moyen de paiement Mobile Money :",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mobile Money Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PaymentProviderCard(
                            name = "Wave",
                            badgeColor = WaveCyan,
                            isSelected = selectedProvider == "Wave",
                            onClick = { onSelectProvider("Wave") },
                            modifier = Modifier.weight(1f)
                        )

                        PaymentProviderCard(
                            name = "Orange Money",
                            badgeColor = OrangeMoney,
                            isSelected = selectedProvider == "Orange Money",
                            onClick = { onSelectProvider("Orange Money") },
                            modifier = Modifier.weight(1f)
                        )

                        PaymentProviderCard(
                            name = "Free Money",
                            badgeColor = Color(0xFFC0392B),
                            isSelected = selectedProvider == "Free Money",
                            onClick = { onSelectProvider("Free Money") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone recap
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = OceanBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Numéro débité : $buyerPhone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onConfirmPurchase,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedProvider == "Wave") WaveCyan else CtaOrange
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_payment_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Payer $totalAmount FCFA avec $selectedProvider",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentProviderCard(
    name: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(badgeColor)) else CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .clickable { onClick() }
            .height(64.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = if (isSelected) badgeColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
