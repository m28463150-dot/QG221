package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val phone: String,
    val username: String,
    val role: String = "listener", // "listener", "artist", "admin", "vigil"
    val isVerified: Boolean = false,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val artistName: String,
    val title: String,
    val feat: String = "",
    val genre: String, // "Mbalax", "Rap Galsen", "Afrobeat", "Amapiano", "Acoustic"
    val city: String = "Dakar", // "Dakar", "Saint-Louis", "Rufisque", "Thiès"
    val durationSec: Int = 210,
    val plays: Int = 1240,
    val likes: Int = 340,
    val audioQuality: String = "HIGH", // "LOW" (128kbps) or "HIGH" (320kbps)
    val coverResName: String = "",
    val status: String = "approved", // "approved", "pending", "rejected"
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val lyrics: String = ""
) {
    val playsCount: Int get() = plays
    val isApproved: Boolean get() = status == "approved"
}

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val artistName: String,
    val title: String,
    val posterResName: String = "event_guet_ndar",
    val venueName: String, // e.g. "Quai de Guet Ndar, Saint-Louis"
    val city: String,
    val dateTimeText: String,
    val dateTimeMillis: Long,
    val description: String,
    val status: String = "approved", // "approved", "pending", "rejected"
    val totalCapacity: Int = 200,
    val soldCount: Int = 68
)

@Entity(tableName = "event_tracks", primaryKeys = ["eventId", "trackId"])
data class EventTrackCrossRef(
    val eventId: String,
    val trackId: String
)

@Entity(tableName = "ticket_types")
data class TicketTypeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val name: String, // "Standard", "VIP", "Early Bird"
    val priceCfa: Int, // e.g. 3000, 5000, 10000
    val totalQty: Int,
    val soldQty: Int = 0
)

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ticketTypeId: String,
    val ticketTypeName: String,
    val eventId: String,
    val eventTitle: String,
    val eventDateText: String,
    val venueName: String,
    val buyerId: String,
    val buyerPhone: String,
    val qrSecretHash: String,
    val status: String = "valid", // "valid", "scanned", "transferred"
    val scannedAtMillis: Long? = null,
    val transferredToPhone: String? = null
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String, // "purchase", "payout", "commission"
    val amountCfa: Int,
    val provider: String, // "Wave", "Orange Money", "Free Money"
    val providerTransactionId: String,
    val status: String = "SUCCESS",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val paymentProvider: String get() = provider
    val commissionCfa: Int get() = (amountCfa * 0.10).toInt()
    val buyerPhone: String get() = userId
    val createdAtText: String get() = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRANCE).format(java.util.Date(timestamp))
}

@Entity(tableName = "artist_wallets")
data class ArtistWalletEntity(
    @PrimaryKey val artistId: String,
    val stageName: String,
    val balanceCfa: Int = 0,
    val pendingWithdrawalCfa: Int = 0,
    val totalTicketsSold: Int = 0,
    val mobileMoneyPhone: String = "+221 77 000 00 00",
    val provider: String = "Wave"
) {
    val totalGrossSalesCfa: Int get() = if (balanceCfa > 0) (balanceCfa / 0.9).toInt() else 0
}

data class TrackWithEvent(
    val track: TrackEntity,
    val linkedEvent: EventEntity? = null
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val coverResName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: String,
    val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: String = "info", // "concert_reminder", "new_track", "ticket_success", "info"
    val targetId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class ArtistBio(
    val id: String,
    val stageName: String,
    val realName: String,
    val city: String,
    val bio: String,
    val quote: String,
    val genres: List<String>,
    val followersCount: Int,
    val monthlyListeners: Int
)

@Entity(tableName = "track_comments")
data class TrackCommentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val trackId: String,
    val authorName: String,
    val text: String,
    val timestampSec: Int = 0, // 0 if general comment, or specific second of track
    val timestampText: String = "", // e.g. "01:45"
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "artist_tips")
data class ArtistTipEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val artistName: String,
    val trackId: String? = null,
    val trackTitle: String? = null,
    val senderName: String,
    val amountCfa: Int,
    val provider: String = "Wave", // "Wave", "Orange Money"
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class EqualizerProfile(
    val id: String,
    val name: String,
    val bassBoostPercent: Int, // 0 to 100
    val bandsDb: List<Float> // 5 bands in dB: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
) {
    companion object {
        val PRESETS = listOf(
            EqualizerProfile("mbalax", "Mbalax", 50, listOf(5f, 3f, 0f, 4f, 5f)),
            EqualizerProfile("hiphop", "Hip-Hop", 65, listOf(7f, 4f, -1.5f, 2.5f, 4f)),
            EqualizerProfile("acoustique", "Acoustique", 20, listOf(2f, 2f, 4f, 3f, 2f)),
            EqualizerProfile("vocal", "Vocal", 15, listOf(-2f, 1f, 4f, 3f, 1f)),
            EqualizerProfile("flat", "Équilibré", 0, listOf(0f, 0f, 0f, 0f, 0f)),
            EqualizerProfile("custom", "Personnalisé", 30, listOf(0f, 0f, 0f, 0f, 0f))
        )
    }
}

data class ChartRankItem(
    val track: TrackEntity,
    val rank: Int,
    val previousRank: Int,
    val trend: String, // "UP", "DOWN", "SAME", "NEW"
    val weeklyStreams: Int
)


