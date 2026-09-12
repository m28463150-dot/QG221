package com.example.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppNotificationEntity
import com.example.ui.theme.*

@Composable
fun NotificationDialog(
    notifications: List<AppNotificationEntity>,
    onDismiss: () -> Unit,
    onMarkAllAsRead: () -> Unit,
    onTestNotification: () -> Unit,
    onNotificationClick: (AppNotificationEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("notification_center_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = OceanBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Centre d'Alertes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Sub-actions: Mark read and Test push
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onMarkAllAsRead,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = OceanBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tout marquer comme lu", fontSize = 11.sp, color = OceanBlue)
                    }

                    OutlinedButton(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = CtaOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simuler Rappel", fontSize = 11.sp, color = CtaOrange)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune notification pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationItemRow(
                                notification = notif,
                                onClick = { onNotificationClick(notif) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanBlue)
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
private fun NotificationItemRow(
    notification: AppNotificationEntity,
    onClick: () -> Unit
) {
    val isReminder = notification.type == "concert_reminder"
    val isNewTrack = notification.type == "new_track"

    val iconColor = when {
        isReminder -> CtaOrange
        isNewTrack -> NetYellowDark
        else -> OceanBlue
    }

    val iconVector = when {
        isReminder -> Icons.Default.Event
        isNewTrack -> Icons.Default.MusicNote
        else -> Icons.Default.Info
    }

    Surface(
        color = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else OceanBlue.copy(alpha = 0.08f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CtaOrange)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
