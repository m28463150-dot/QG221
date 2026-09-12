package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.EventEntity
import com.example.ui.theme.*

@Composable
fun EventsCatalogScreen(
    events: List<EventEntity>,
    onSelectEvent: (EventEntity) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var selectedCity by remember { mutableStateOf("Toutes") }
    val cities = listOf("Toutes", "Saint-Louis", "Rufisque", "Dakar")

    val filteredEvents = remember(events, selectedCity) {
        if (selectedCity == "Toutes") events
        else events.filter { it.city.equals(selectedCity, ignoreCase = true) }
    }

    LazyColumn(
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
            .testTag("events_catalog_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
                Text(
                    text = "Billetterie Live 221",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = OceanBlue
                )
                Text(
                    text = "Achetez vos tickets officiels Wave / Orange Money sans frais cachés.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // City Filters
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                cities.forEach { c ->
                    FilterChip(
                        selected = selectedCity == c,
                        onClick = { selectedCity = c },
                        label = { Text(c, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (filteredEvents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun événement prévu dans cette ville pour le moment.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            items(filteredEvents) { event ->
                val remaining = event.totalCapacity - event.soldCount
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectEvent(event) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.event_guet_ndar),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )

                            Surface(
                                color = CtaOrange,
                                shape = RoundedCornerShape(bottomStart = 8.dp),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = "OFFICIEL QUAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = OceanBlue, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Par ${event.artistName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = OceanBlue
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = NetYellowDark, modifier = Modifier.size(14.dp))
                                Text(text = event.venueName, fontSize = 12.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(13.dp))
                                Text(text = event.dateTimeText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$remaining places restantes",
                                    fontSize = 12.sp,
                                    color = if (remaining < 20) ErrorRed else SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { onSelectEvent(event) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CtaOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Réserver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
