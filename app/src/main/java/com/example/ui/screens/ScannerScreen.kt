package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.TicketEntity
import com.example.data.repository.QuayGuetRepository
import com.example.ui.theme.*

@Composable
fun ScannerScreen(
    events: List<EventEntity>,
    selectedEventId: String?,
    allTickets: List<TicketEntity>,
    lastScanResult: QuayGuetRepository.ScanResult?,
    onSelectEvent: (String) -> Unit,
    onScanCode: (String) -> Unit,
    onClearScanResult: () -> Unit
) {
    val context = LocalContext.current
    var manualInputCode by remember { mutableStateOf("") }

    // Trigger haptic vibration on result change
    LaunchedEffect(lastScanResult) {
        if (lastScanResult != null) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (lastScanResult) {
                        is QuayGuetRepository.ScanResult.Valid ->
                            VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                        else ->
                            VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 200), -1)
                    }
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scanner_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Mode Vigile & Concert Filter
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = OceanBlueDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NetYellow)
                            Text(
                                text = "MODE SCANNER VIGILE",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = SandWhite
                            )
                        }

                        Surface(
                            color = SuccessGreen,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "CACHE HORS-LIGNE ACTIF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Événement à contrôler à l'entrée :",
                        style = MaterialTheme.typography.bodySmall,
                        color = SandWhite.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(events) { evt ->
                            val isSel = evt.id == selectedEventId
                            FilterChip(
                                selected = isSel,
                                onClick = { onSelectEvent(evt.id) },
                                label = { Text(evt.title, fontSize = 11.sp, maxLines = 1) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NetYellow,
                                    selectedLabelColor = OceanBlueDark,
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    labelColor = SandWhite
                                )
                            )
                        }
                    }
                }
            }

            // Viewfinder Camera Simulation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Reticle
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .border(2.dp, NetYellow, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = NetYellow,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Pointez vers le QR du spectateur",
                                color = SandWhite,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Bottom fast input bar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = manualInputCode,
                                onValueChange = { manualInputCode = it },
                                placeholder = { Text("Code Billet ou UUID...", fontSize = 12.sp, color = Color.Gray) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (manualInputCode.isNotBlank()) {
                                        onScanCode(manualInputCode.trim())
                                        manualInputCode = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NetYellow)
                            ) {
                                Text("Scanner", color = OceanBlueDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Testing Bar (Instant tap on any existing ticket to test the 3 states: Valid, Already Scanned, Invalid)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Billets disponibles pour test immédiat du Vigile :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allTickets) { tkt ->
                            SuggestionChip(
                                onClick = { onScanCode(tkt.id) },
                                label = {
                                    Text(
                                        text = "${tkt.id.take(12)} (${tkt.ticketTypeName.take(5)})",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (tkt.status == "scanned") ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Also add an invalid fake ticket test
                        item {
                            SuggestionChip(
                                onClick = { onScanCode("FAKE-TICKET-999") },
                                label = { Text("Faux Billet", fontSize = 11.sp, color = ErrorRed) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        // =========================================================================
        // FULL-SCREEN VALIDATION OVERLAYS (GREEN OR RED AS REQUIRED BY B5!)
        // =========================================================================
        AnimatedVisibility(
            visible = lastScanResult != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            when (val res = lastScanResult) {
                is QuayGuetRepository.ScanResult.Valid -> {
                    // FULL SCREEN GREEN VALIDATION
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SuccessGreen)
                            .clickable { onClearScanResult() }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(72.dp)
                                )
                            }

                            Text(
                                text = "ACCÈS AUTORISÉ",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = res.ticket.ticketTypeName.uppercase(),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = OceanBlue
                                    )
                                    Text(
                                        text = "Concert : ${res.ticket.eventTitle}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Billet ID : ${res.ticket.id}",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            Text(
                                text = "Touchez l'écran pour scanner le spectateur suivant",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                is QuayGuetRepository.ScanResult.AlreadyScanned -> {
                    // FULL SCREEN RED ALREADY SCANNED
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ErrorRed)
                            .clickable { onClearScanResult() }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(72.dp)
                                )
                            }

                            Text(
                                text = "ACCÈS REFUSÉ !",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = res.scannedAtText,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = ErrorRed,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Type : ${res.ticket.ticketTypeName} • #${res.ticket.id}",
                                        fontSize = 12.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Fraude potentielle : Ce billet a déjà franchi le tourniquet !",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Text(
                                text = "Touchez l'écran pour fermer",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                is QuayGuetRepository.ScanResult.Invalid -> {
                    // FULL SCREEN RED INVALID
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ErrorRed)
                            .clickable { onClearScanResult() }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(72.dp)
                                )
                            }

                            Text(
                                text = "BILLET INVALIDE",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = res.reason,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ErrorRed,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Text(
                                text = "Touchez pour continuer",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                null -> {}
            }
        }
    }
}
