package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE status = 'approved' ORDER BY plays DESC")
    fun getAllApprovedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE status = 'approved' AND city = :city ORDER BY plays DESC")
    fun getTracksByCity(city: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE status = 'approved' ORDER BY id DESC LIMIT 10")
    fun getRecentTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE status = 'approved' ORDER BY plays DESC LIMIT 10")
    fun getTop10Tracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1")
    fun getDownloadedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE artistId = :artistId")
    fun getTracksByArtist(artistId: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE status = 'pending'")
    fun getPendingTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("UPDATE tracks SET isDownloaded = :downloaded, localFilePath = :path WHERE id = :trackId")
    suspend fun setDownloaded(trackId: String, downloaded: Boolean, path: String?)

    @Query("UPDATE tracks SET status = :status WHERE id = :trackId")
    suspend fun updateStatus(trackId: String, status: String)

    @Query("UPDATE tracks SET plays = plays + 1 WHERE id = :trackId")
    suspend fun incrementPlays(trackId: String)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE status = 'approved' ORDER BY dateTimeMillis ASC")
    fun getAllApprovedEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE artistId = :artistId")
    fun getEventsByArtist(artistId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE status = 'pending'")
    fun getPendingEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE events SET status = :status WHERE id = :eventId")
    suspend fun updateStatus(eventId: String, status: String)

    @Query("UPDATE events SET soldCount = soldCount + :qty WHERE id = :eventId")
    suspend fun incrementSoldCount(eventId: String, qty: Int)

    // Event-Track CrossRef for "Liaison Musicale"
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkTrackToEvent(ref: EventTrackCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkTracksToEvent(refs: List<EventTrackCrossRef>)

    @Query("""
        SELECT e.* FROM events e 
        INNER JOIN event_tracks et ON e.id = et.eventId 
        WHERE et.trackId = :trackId AND e.status = 'approved' 
        LIMIT 1
    """)
    suspend fun getEventForTrack(trackId: String): EventEntity?

    @Query("""
        SELECT t.* FROM tracks t 
        INNER JOIN event_tracks et ON t.id = et.trackId 
        WHERE et.eventId = :eventId
    """)
    fun getTracksForEvent(eventId: String): Flow<List<TrackEntity>>

    // Ticket types
    @Query("SELECT * FROM ticket_types WHERE eventId = :eventId")
    fun getTicketTypesForEvent(eventId: String): Flow<List<TicketTypeEntity>>

    @Query("SELECT * FROM ticket_types WHERE eventId = :eventId")
    suspend fun getTicketTypesForEventSync(eventId: String): List<TicketTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketTypes(types: List<TicketTypeEntity>)

    @Query("UPDATE ticket_types SET soldQty = soldQty + :qty WHERE id = :ticketTypeId")
    suspend fun incrementTicketTypeSold(ticketTypeId: String, qty: Int)
}

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets WHERE buyerId = :buyerId OR buyerPhone = :phone ORDER BY id DESC")
    fun getTicketsForUser(buyerId: String, phone: String): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: String): TicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<TicketEntity>)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("SELECT COUNT(*) FROM tickets")
    fun getTotalTicketsSoldCount(): Flow<Int>

    @Query("SELECT * FROM tickets WHERE eventId = :eventId")
    suspend fun getTicketsForEvent(eventId: String): List<TicketEntity>
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Wallet
    @Query("SELECT * FROM artist_wallets WHERE artistId = :artistId")
    suspend fun getArtistWallet(artistId: String): ArtistWalletEntity?

    @Query("SELECT * FROM artist_wallets WHERE artistId = :artistId")
    fun getArtistWalletFlow(artistId: String): Flow<ArtistWalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: ArtistWalletEntity)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: String): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deletePlaylistCrossRefs(playlistId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN playlist_tracks pt ON t.id = pt.trackId
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.addedAt ASC
    """)
    fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getTrackCount(playlistId: String): Flow<Int>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)
}

