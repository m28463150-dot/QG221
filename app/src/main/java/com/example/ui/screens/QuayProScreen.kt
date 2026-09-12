package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuayProScreen(
    isProUser: Boolean,
    onSubscribe: (plan: String, provider: String) -> Unit,
    onCancelSubscription: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var selectedPlan by remember { mutableStateOf("annual") } // "monthly" or "annual"
    var selectedProvider by remember { mutableStateOf("Wave") } // "Wave" or "Orange Money"
    var isProcessing by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            Color(0xFFFFFDF5),
            Color(0xFFF9F6EE),
            Color(0xFFF0F5FA)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .testTag("quay_pro_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with back button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = OceanBlue)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(ProGold, ProAmber))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = ProGoldDark, modifier = Modifier.size(16.dp))
                            Text(
                                text = "QUAY GUET PRO 221",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = OceanBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            // Hero Golden Gradient Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        QuayGreenDark,
                                        QuayGreen,
                                        Color(0xFF043324)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .padding(3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.quay_guett_logo),
                                        contentDescription = "Logo Quay Guett 221",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(ProGold, ProAmber, Color(0xFFD4A30B))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "PRO",
                                        tint = QuayGreenDark,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isProUser) "VOUS ÊTES MEMBRE PRO" else "PASSEZ À LA VITESSE PRO",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = ProGold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isProUser)
                                    "Abonnement actif avec pass VIP concerts et FLAC illimité."
                                else
                                    "La musique sénégalaise en haute fidélité. Le live sans attente.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SandWhite.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // PRO Status Card if already subscribed
            if (isProUser) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ProGold)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                    Text(
                                        text = "Statut : PRO ACTIF",
                                        fontWeight = FontWeight.Bold,
                                        color = OceanBlue
                                    )
                                }
                                Surface(
                                    color = ProGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "VIP PASS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = ProGoldDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Renouvellement automatique : 12 Septembre 2027\nMode audio par défaut : Haute Qualité 320k & FLAC débloqués.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            OutlinedButton(
                                onClick = onCancelSubscription,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Résilier ou basculer en mode standard", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Features Grid
            item {
                Text(
                    text = "Avantages Exclusifs QUAY PRO",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = OceanBlue
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProFeatureRow(
                        icon = Icons.Default.HighQuality,
                        iconTint = ProGoldDark,
                        title = "Audio Sans Perte (FLAC & 320 kbps)",
                        description = "Clarté acoustique intégrale pour apprécier la subtilité des percussions et instruments traditionnels."
                    )

                    ProFeatureRow(
                        icon = Icons.Default.DownloadDone,
                        iconTint = OceanBlue,
                        title = "Téléchargements Hors-Ligne Illimités",
                        description = "Enregistrez autant de titres et concerts que vous le souhaitez, sans limitation d'espace ni data."
                    )

                    ProFeatureRow(
                        icon = Icons.Default.ConfirmationNumber,
                        iconTint = CtaOrange,
                        title = "Coupe-File VIP & Accès Prioritaire",
                        description = "Entrez directement par la porte VIP aux concerts de Saint-Louis et Rufisque avec votre billet doré."
                    )

                    ProFeatureRow(
                        icon = Icons.Default.Percent,
                        iconTint = SuccessGreen,
                        title = "-10% sur Toutes les Billetteries",
                        description = "Réduction automatique immédiate appliquée sur vos tickets officiels Wave et Orange Money."
                    )

                    ProFeatureRow(
                        icon = Icons.Default.Block,
                        iconTint = ProAmber,
                        title = "Zéro Publicité & Écoute Continue",
                        description = "Transition fluide entre les sons sans coupure ni annonce commerciale."
                    )

                    ProFeatureRow(
                        icon = Icons.Default.Verified,
                        iconTint = ProGold,
                        title = "Badge Doré Officiel QUAY PRO",
                        description = "Affiché sur votre profil pour marquer votre soutien à la musique sénégalaise."
                    )
                }
            }

            // Plans Selector (if not yet Pro)
            if (!isProUser) {
                item {
                    Text(
                        text = "Choisissez Votre Formule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = OceanBlue
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Annual Plan
                        ProPlanCard(
                            title = "ANNUEL",
                            badge = "2 MOIS OFFERTS",
                            price = "19 000 FCFA",
                            period = "/ an (soit 1 583 F/mois)",
                            isSelected = selectedPlan == "annual",
                            onClick = { selectedPlan = "annual" },
                            modifier = Modifier.weight(1f)
                        )

                        // Monthly Plan
                        ProPlanCard(
                            title = "MENSUEL",
                            badge = "SANS ENGAGEMENT",
                            price = "2 000 FCFA",
                            period = "/ mois",
                            isSelected = selectedPlan == "monthly",
                            onClick = { selectedPlan = "monthly" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Payment Provider Choice
                item {
                    Text(
                        text = "Règlement Sécurisé Mobile Money",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = OceanBlue
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = "Wave" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedProvider == "Wave") WaveCyan.copy(alpha = 0.15f) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                if (selectedProvider == "Wave") 2.dp else 1.dp,
                                if (selectedProvider == "Wave") WaveCyan else Color.LightGray.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(selected = selectedProvider == "Wave", onClick = { selectedProvider = "Wave" })
                                Text(
                                    text = "Wave Sénégal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OceanBlue
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = "Orange Money" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedProvider == "Orange Money") OrangeMoney.copy(alpha = 0.15f) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                if (selectedProvider == "Orange Money") 2.dp else 1.dp,
                                if (selectedProvider == "Orange Money") OrangeMoney else Color.LightGray.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(selected = selectedProvider == "Orange Money", onClick = { selectedProvider = "Orange Money" })
                                Text(
                                    text = "Orange Money",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OceanBlue
                                )
                            }
                        }
                    }
                }

                // CTA Button
                item {
                    Button(
                        onClick = {
                            isProcessing = true
                            onSubscribe(selectedPlan, selectedProvider)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("subscribe_pro_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ProGoldDark, ProAmber, CtaOrange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                                Text(
                                    text = if (selectedPlan == "annual") "ACTIVER PRO (19 000 F / AN)" else "ACTIVER PRO (2 000 F / MOIS)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
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
fun ProFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EEF3))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = OceanBlue
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ProPlanCard(
    title: String,
    badge: String,
    price: String,
    period: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color(0xFFF9FBFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) ProGoldDark else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = if (isSelected) ProGold.copy(alpha = 0.25f) else Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) ProGoldDark else Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OceanBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = if (isSelected) ProGoldDark else OceanBlue
            )

            Text(
                text = period,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
