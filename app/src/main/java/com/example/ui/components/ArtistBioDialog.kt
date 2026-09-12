package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.ArtistBio
import com.example.ui.theme.*

@Composable
fun ArtistBioDialog(
    artistBio: ArtistBio,
    onDismiss: () -> Unit,
    onPlayArtistTracks: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("artist_bio_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with visual banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(OceanBlue, OceanBlueDark)
                            )
                        )
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }

                    // Centered circular avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(NightBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.quay_guett_logo),
                                contentDescription = "Avatar Artiste",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(46.dp))

                // Artist Name & City
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = artistBio.stageName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Artiste Vérifié",
                            tint = NetYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "${artistBio.realName} • ${artistBio.city}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quote
                    Surface(
                        color = OceanBlue.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = artistBio.quote,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = OceanBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${artistBio.monthlyListeners / 1000}k",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Auditeurs/mois",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${artistBio.followersCount / 1000}k",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NetYellowDark
                            )
                            Text(
                                text = "Abonnés Quai",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Genres
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        artistBio.genres.forEach { genre ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NetYellow.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = genre,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlueDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bio Text
                    Text(
                        text = "Biographie & Parcours",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = artistBio.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Close or Listen Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue)
                    ) {
                        Text("Fermer la fiche artiste")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}
