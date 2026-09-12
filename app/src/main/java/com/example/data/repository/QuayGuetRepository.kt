package com.example.data.repository

import android.content.Context
import com.example.data.db.QuayGuetDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.UUID

class QuayGuetRepository(private val context: Context) {
    private val db = QuayGuetDatabase.getInstance(context)
    private val trackDao = db.trackDao()
    private val eventDao = db.eventDao()
    private val ticketDao = db.ticketDao()
    private val userDao = db.userDao()
    private val transactionDao = db.transactionDao()
    private val playlistDao = db.playlistDao()
    private val notificationDao = db.notificationDao()

    // Offline scanner local memory cache for network drops during concerts
    private val localScannedCache = mutableMapOf<String, Long>()

    suspend fun initSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingUser = userDao.getUserByPhone("+221 77 123 45 67")
        if (existingUser != null) return@withContext

        // 1. Seed Users
        val modou = UserEntity(
            id = "user_modou",
            phone = "+221 77 123 45 67",
            username = "modou_rufisque",
            role = "listener",
            isVerified = false
        )
        val awa = UserEntity(
            id = "artist_awa",
            phone = "+221 78 456 78 90",
            username = "awa_ndar",
            role = "artist",
            isVerified = true
        )
        val ngaaka = UserEntity(
            id = "artist_ngaaka",
            phone = "+221 77 777 88 99",
            username = "ngaaka_blinde",
            role = "artist",
            isVerified = true
        )
        val vigile = UserEntity(
            id = "user_vigile",
            phone = "+221 76 999 00 11",
            username = "vigile_quai",
            role = "vigil",
            isVerified = true
        )
        val admin = UserEntity(
            id = "user_admin",
            phone = "+221 77 000 00 00",
            username = "admin_quay221",
            role = "admin",
            isVerified = true
        )
        userDao.insertUser(modou)
        userDao.insertUser(awa)
        userDao.insertUser(ngaaka)
        userDao.insertUser(vigile)
        userDao.insertUser(admin)

        // 2. Seed Tracks
        val tracks = listOf(
            TrackEntity(
                id = "track_1",
                artistId = "artist_awa",
                artistName = "Awa Ndar",
                title = "Guet Ndar Blues",
                feat = "Orchestre Fluvial",
                genre = "Acoustic",
                city = "Saint-Louis",
                durationSec = 215,
                plays = 14200,
                likes = 3100,
                status = "approved",
                lyrics = "Ndoxum Guet Ndar, ndoxum tawfekh... Jekk naa ci Quay bi ak sama gaal."
            ),
            TrackEntity(
                id = "track_2",
                artistId = "artist_ngaaka",
                artistName = "Ngaaka Blindé",
                title = "Galsen Flow (Rufisque to Dakar)",
                feat = "Awa Ndar",
                genre = "Rap Galsen",
                city = "Dakar",
                durationSec = 198,
                plays = 38900,
                likes = 9400,
                status = "approved",
                lyrics = "Dakar bou bakh, Rufisque ci kanam, Quay 221 ci biir beat bi."
            ),
            TrackEntity(
                id = "track_3",
                artistId = "artist_jeeba",
                artistName = "Jeeba",
                title = "Loolu (Ndar Love)",
                feat = "",
                genre = "Afrobeat",
                city = "Saint-Louis",
                durationSec = 224,
                plays = 29500,
                likes = 7800,
                status = "approved"
            ),
            TrackEntity(
                id = "track_4",
                artistId = "artist_amadeus",
                artistName = "Amadeus",
                title = "Sama Xol",
                feat = "Galsen Melodies",
                genre = "Mbalax",
                city = "Dakar",
                durationSec = 205,
                plays = 18700,
                likes = 4500,
                status = "approved"
            ),
            TrackEntity(
                id = "track_5",
                artistId = "artist_vj",
                artistName = "VJ",
                title = "Dakar Night Vibe",
                feat = "",
                genre = "Amapiano",
                city = "Dakar",
                durationSec = 185,
                plays = 24100,
                likes = 6200,
                status = "approved"
            ),
            TrackEntity(
                id = "track_6",
                artistId = "artist_dip",
                artistName = "Dip Doundou Guiss",
                title = "Rufisque Highway",
                feat = "",
                genre = "Rap Galsen",
                city = "Rufisque",
                durationSec = 240,
                plays = 41200,
                likes = 11300,
                status = "approved"
            ),
            TrackEntity(
                id = "track_7",
                artistId = "artist_wally",
                artistName = "Wally Seck",
                title = "Baila Thiès",
                feat = "",
                genre = "Mbalax",
                city = "Thiès",
                durationSec = 260,
                plays = 55400,
                likes = 14200,
                status = "approved"
            ),
            TrackEntity(
                id = "track_8",
                artistId = "artist_mia",
                artistName = "Mia Guissé",
                title = "Yaye Boye (Saint-Louis Mix)",
                feat = "",
                genre = "Afrobeat",
                city = "Saint-Louis",
                durationSec = 192,
                plays = 22100,
                likes = 5400,
                status = "approved"
            )
        )
        trackDao.insertTracks(tracks)

        // 3. Seed Events
        val event1 = EventEntity(
            id = "event_guet_ndar",
            artistId = "artist_awa",
            artistName = "Awa Ndar",
            title = "Nuit du Quai - Saint-Louis Live",
            posterResName = "event_guet_ndar",
            venueName = "Quai de Guet Ndar, Saint-Louis",
            city = "Saint-Louis",
            dateTimeText = "Samedi 26 Septembre 2026 - 21h00",
            dateTimeMillis = System.currentTimeMillis() + 86400000L * 14,
            description = "Grand concert live exceptionnel sur les berges du fleuve Sénégal à Guet Ndar. Venez vibrer au rythme du Mbalax acoustique et de l'Afrobeat au milieu des pirogues traditionnelles. Scène flottante illuminée.",
            status = "approved",
            totalCapacity = 150,
            soldCount = 92
        )
        val event2 = EventEntity(
            id = "event_just4u",
            artistId = "artist_ngaaka",
            artistName = "Ngaaka Blindé",
            title = "Rap Galsen Jam: Dakar to Rufisque",
            posterResName = "quay_hero_banner",
            venueName = "Just 4 U, Point E, Dakar",
            city = "Dakar",
            dateTimeText = "Vendredi 2 Octobre 2026 - 22h00",
            dateTimeMillis = System.currentTimeMillis() + 86400000L * 20,
            description = "Le plus gros rendez-vous Rap Galsen de la rentrée à Dakar avec Ngaaka Blindé et invités spéciaux. Ambiance 100% hip hop sénégalais.",
            status = "approved",
            totalCapacity = 300,
            soldCount = 140
        )
        val event3 = EventEntity(
            id = "event_sunset_ndar",
            artistId = "artist_awa",
            artistName = "Awa Ndar",
            title = "Acoustic Sunset au Fleuve",
            posterResName = "event_guet_ndar",
            venueName = "Hôtel de la Poste, Saint-Louis",
            city = "Saint-Louis",
            dateTimeText = "Dimanche 11 Octobre 2026 - 18h30",
            dateTimeMillis = System.currentTimeMillis() + 86400000L * 29,
            description = "Concert intime au coucher du soleil face au pont Faidherbe. Showcase acoustique exclusif limité à 80 places.",
            status = "approved",
            totalCapacity = 80,
            soldCount = 45
        )
        eventDao.insertEvents(listOf(event1, event2, event3))

        // 4. Liaison Musicale (Track <-> Event)
        // Innovation majeure: Track linked to Event triggers "VOIR EN LIVE" CTA in player!
        eventDao.linkTracksToEvent(
            listOf(
                EventTrackCrossRef(eventId = "event_guet_ndar", trackId = "track_1"), // Guet Ndar Blues -> Nuit du Quai
                EventTrackCrossRef(eventId = "event_guet_ndar", trackId = "track_3"), // Loolu -> Nuit du Quai
                EventTrackCrossRef(eventId = "event_just4u", trackId = "track_2")    // Galsen Flow -> Just 4 U
            )
        )

        // 5. Seed Ticket Types
        val ticketTypes = listOf(
            TicketTypeEntity("tt_1_early", "event_guet_ndar", "Early Bird", 2000, 30, 30),
            TicketTypeEntity("tt_1_std", "event_guet_ndar", "Standard", 3500, 80, 48),
            TicketTypeEntity("tt_1_vip", "event_guet_ndar", "VIP (Accès Pirogue)", 7000, 40, 14),

            TicketTypeEntity("tt_2_std", "event_just4u", "Entrée Simple", 3000, 200, 110),
            TicketTypeEntity("tt_2_vip", "event_just4u", "VIP Backstage", 8000, 100, 30),

            TicketTypeEntity("tt_3_std", "event_sunset_ndar", "Place Unique", 5000, 80, 45)
        )
        eventDao.insertTicketTypes(ticketTypes)

        // 6. Seed Sample Tickets for Modou (Auditeur)
        val ticket1Id = "TKT-NDAR-77123-01"
        val ticket2Id = "TKT-NDAR-77123-02"
        val secretHash1 = generateQrHash(ticket1Id, "event_guet_ndar")
        val secretHash2 = generateQrHash(ticket2Id, "event_guet_ndar")

        val modouTickets = listOf(
            TicketEntity(
                id = ticket1Id,
                ticketTypeId = "tt_1_vip",
                ticketTypeName = "VIP (Accès Pirogue)",
                eventId = "event_guet_ndar",
                eventTitle = "Nuit du Quai - Saint-Louis Live",
                eventDateText = "Samedi 26 Septembre 2026 - 21h00",
                venueName = "Quai de Guet Ndar, Saint-Louis",
                buyerId = "user_modou",
                buyerPhone = "+221 77 123 45 67",
                qrSecretHash = secretHash1,
                status = "valid"
            ),
            TicketEntity(
                id = ticket2Id,
                ticketTypeId = "tt_1_std",
                ticketTypeName = "Standard",
                eventId = "event_guet_ndar",
                eventTitle = "Nuit du Quai - Saint-Louis Live",
                eventDateText = "Samedi 26 Septembre 2026 - 21h00",
                venueName = "Quai de Guet Ndar, Saint-Louis",
                buyerId = "user_modou",
                buyerPhone = "+221 77 123 45 67",
                qrSecretHash = secretHash2,
                status = "valid"
            )
        )
        ticketDao.insertTickets(modouTickets)

        // 7. Seed Artist Wallet
        val awaWallet = ArtistWalletEntity(
            artistId = "artist_awa",
            stageName = "Awa Ndar",
            balanceCfa = 364500, // Sales - 10% commission
            pendingWithdrawalCfa = 50000,
            totalTicketsSold = 92,
            mobileMoneyPhone = "+221 78 456 78 90",
            provider = "Wave"
        )
        transactionDao.insertOrUpdateWallet(awaWallet)

        // 8. Seed Initial Transactions
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = "user_modou",
                type = "purchase",
                amountCfa = 10500,
                provider = "Wave",
                providerTransactionId = "WV-SN-89218274",
                description = "Achat 2 tickets Nuit du Quai (1 VIP + 1 Standard)"
            )
        )

        // 9. Seed Initial Playlists
        val playlist1 = PlaylistEntity(
            id = "pl_ndawrabine",
            title = "Ndawrabine Vibes",
            description = "Rythmes ancestraux et cadences envoûtantes de Guet Ndar et de la côte",
            coverResName = "artist_awa"
        )
        val playlist2 = PlaylistEntity(
            id = "pl_saint_louis_jazz",
            title = "Saint-Louis Jazz & Soul",
            description = "Ambiance feutrée du Quai fluvial, entre guitares acoustiques et xalam",
            coverResName = "event_guet_ndar"
        )
        val playlist3 = PlaylistEntity(
            id = "pl_soiree_live",
            title = "Soirée Guet Ndar Live",
            description = "Les pépites les plus chaudes pour faire vibrer le Quai en direct",
            coverResName = "artist_ngaaka"
        )
        playlistDao.insertPlaylist(playlist1)
        playlistDao.insertPlaylist(playlist2)
        playlistDao.insertPlaylist(playlist3)

        // Seed tracks inside playlists
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_ndawrabine", "track_1"))
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_ndawrabine", "track_3"))
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_saint_louis_jazz", "track_1"))
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_saint_louis_jazz", "track_4"))
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_soiree_live", "track_2"))
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef("pl_soiree_live", "track_5"))

        // 10. Seed Initial In-App Notifications
        notificationDao.insertNotification(
            AppNotificationEntity(
                id = "notif_1",
                title = "🔔 Rappel Concert : Nuit du Quai",
                message = "Votre concert « Nuit du Quai Guet Ndar » a lieu demain à 21h00 au Quai de Guet Ndar ! Votre pass QR est prêt hors-ligne.",
                type = "concert_reminder",
                targetId = "event_guet_ndar",
                isRead = false
            )
        )
        notificationDao.insertNotification(
            AppNotificationEntity(
                id = "notif_2",
                title = "🔥 Nouveauté : « Guet Ndar Blues »",
                message = "Le nouveau titre acoustique d'Awa Ndar vient de sortir en FLAC 320k. Écoutez-le dès maintenant !",
                type = "new_track",
                targetId = "track_1",
                isRead = false
            )
        )
        notificationDao.insertNotification(
            AppNotificationEntity(
                id = "notif_3",
                title = "🌊 Bienvenue sur Quay Guett 221",
                message = "Profitez du streaming sans pub et réservez vos billets de concert en 1 clic avec Wave ou Orange Money.",
                type = "info",
                isRead = true
            )
        )
    }

    // Hash helper for QR security (UUID + eventId + secret salt)
    private fun generateQrHash(ticketId: String, eventId: String): String {
        val raw = "$ticketId:$eventId:quay_guet_salt_2026"
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // --- TRACKS & STREAMING ---
    fun getAllTracks(): Flow<List<TrackEntity>> = trackDao.getAllApprovedTracks()
    fun getRecentTracks(): Flow<List<TrackEntity>> = trackDao.getRecentTracks()
    fun getTop10Tracks(): Flow<List<TrackEntity>> = trackDao.getTop10Tracks()
    fun getTracksByCity(city: String): Flow<List<TrackEntity>> = trackDao.getTracksByCity(city)
    fun getDownloadedTracks(): Flow<List<TrackEntity>> = trackDao.getDownloadedTracks()
    fun getTracksByArtist(artistId: String): Flow<List<TrackEntity>> = trackDao.getTracksByArtist(artistId)
    fun getPendingTracks(): Flow<List<TrackEntity>> = trackDao.getPendingTracks()

    suspend fun getTrackById(id: String): TrackEntity? = trackDao.getTrackById(id)

    suspend fun incrementPlays(trackId: String) = trackDao.incrementPlays(trackId)

    // Contextual liaison: find if playing track has an associated live concert!
    suspend fun getLinkedEventForTrack(trackId: String): EventEntity? = withContext(Dispatchers.IO) {
        eventDao.getEventForTrack(trackId)
    }

    // Offline caching of track
    suspend fun toggleDownloadTrack(trackId: String, currentDownloaded: Boolean): Boolean = withContext(Dispatchers.IO) {
        val target = !currentDownloaded
        val path = if (target) {
            val file = File(context.filesDir, "audio_quay_$trackId.bin")
            if (!file.exists()) {
                file.writeBytes("QUAY_GUET_ENCRYPTED_AUDIO_CACHE_$trackId".toByteArray())
            }
            file.absolutePath
        } else {
            val file = File(context.filesDir, "audio_quay_$trackId.bin")
            if (file.exists()) file.delete()
            null
        }
        trackDao.setDownloaded(trackId, target, path)
        target
    }

    suspend fun clearAllDownloads() = withContext(Dispatchers.IO) {
        val downloaded = db.openHelper.readableDatabase
        val files = context.filesDir.listFiles { _, name -> name.startsWith("audio_quay_") }
        files?.forEach { it.delete() }
        val tracks = trackDao.getDownloadedTracks()
        // update in db
        val dbTracks = trackDao.getDownloadedTracks()
        // manual query update
        db.compileStatement("UPDATE tracks SET isDownloaded = 0, localFilePath = NULL").executeUpdateDelete()
    }

    // Upload track (Artist space)
    suspend fun uploadTrack(
        artistId: String,
        artistName: String,
        title: String,
        feat: String,
        genre: String,
        city: String,
        lyrics: String
    ): TrackEntity = withContext(Dispatchers.IO) {
        val track = TrackEntity(
            artistId = artistId,
            artistName = artistName,
            title = title,
            feat = feat,
            genre = genre,
            city = city,
            durationSec = (160..240).random(),
            plays = 0,
            likes = 0,
            status = "approved", // auto approved for instant testing or pending
            lyrics = lyrics
        )
        trackDao.insertTrack(track)
        track
    }

    // --- EVENTS & BILLETTERIE ---
    fun getAllEvents(): Flow<List<EventEntity>> = eventDao.getAllApprovedEvents()
    fun getPendingEvents(): Flow<List<EventEntity>> = eventDao.getPendingEvents()
    fun getEventsByArtist(artistId: String): Flow<List<EventEntity>> = eventDao.getEventsByArtist(artistId)

    suspend fun getEventById(id: String): EventEntity? = eventDao.getEventById(id)
    fun getTracksForEvent(eventId: String): Flow<List<TrackEntity>> = eventDao.getTracksForEvent(eventId)
    fun getTicketTypesForEvent(eventId: String): Flow<List<TicketTypeEntity>> = eventDao.getTicketTypesForEvent(eventId)

    // Create Event (Wizard 4 steps)
    suspend fun createEventWithTickets(
        artistId: String,
        artistName: String,
        title: String,
        venueName: String,
        city: String,
        dateTimeText: String,
        description: String,
        ticketTypes: List<Pair<String, Pair<Int, Int>>>, // Name, (Price, Qty)
        linkedTrackIds: List<String>
    ): EventEntity = withContext(Dispatchers.IO) {
        val eventId = "evt_" + UUID.randomUUID().toString().take(8)
        val totalCap = ticketTypes.sumOf { it.second.second }
        val event = EventEntity(
            id = eventId,
            artistId = artistId,
            artistName = artistName,
            title = title,
            posterResName = "event_guet_ndar",
            venueName = venueName,
            city = city,
            dateTimeText = dateTimeText,
            dateTimeMillis = System.currentTimeMillis() + 86400000L * 10,
            description = description,
            status = "approved", // approved so it appears immediately
            totalCapacity = totalCap,
            soldCount = 0
        )
        eventDao.insertEvent(event)

        // Insert ticket types
        val entities = ticketTypes.map { (name, priceQty) ->
            TicketTypeEntity(
                eventId = eventId,
                name = name,
                priceCfa = priceQty.first,
                totalQty = priceQty.second,
                soldQty = 0
            )
        }
        eventDao.insertTicketTypes(entities)

        // Liaison Musicale!
        val refs = linkedTrackIds.map { trackId ->
            EventTrackCrossRef(eventId = eventId, trackId = trackId)
        }
        if (refs.isNotEmpty()) {
            eventDao.linkTracksToEvent(refs)
        }

        event
    }

    // Purchase tickets (PayDunya / Wave / OM flow)
    data class PurchaseResult(
        val success: Boolean,
        val transactionId: String,
        val tickets: List<TicketEntity>,
        val errorMessage: String? = null
    )

    suspend fun purchaseTickets(
        buyerId: String,
        buyerPhone: String,
        eventId: String,
        selectedTickets: Map<TicketTypeEntity, Int>, // Type -> Quantity
        provider: String // "Wave", "Orange Money", "Free Money"
    ): PurchaseResult = withContext(Dispatchers.IO) {
        val event = eventDao.getEventById(eventId) ?: return@withContext PurchaseResult(false, "", emptyList(), "Événement introuvable")
        val totalAmount = selectedTickets.entries.sumOf { it.key.priceCfa * it.value }
        val totalTicketsCount = selectedTickets.values.sum()

        if (totalTicketsCount <= 0) {
            return@withContext PurchaseResult(false, "", emptyList(), "Veuillez choisir au moins 1 billet")
        }

        val txId = "TX-PAYDUNYA-" + UUID.randomUUID().toString().take(8).uppercase()

        // Generate tickets
        val generatedTickets = mutableListOf<TicketEntity>()
        selectedTickets.forEach { (type, qty) ->
            for (i in 1..qty) {
                val tktId = "QG-${event.city.take(3).uppercase()}-${UUID.randomUUID().toString().take(6).uppercase()}"
                val hash = generateQrHash(tktId, eventId)
                val ticket = TicketEntity(
                    id = tktId,
                    ticketTypeId = type.id,
                    ticketTypeName = type.name,
                    eventId = event.id,
                    eventTitle = event.title,
                    eventDateText = event.dateTimeText,
                    venueName = event.venueName,
                    buyerId = buyerId,
                    buyerPhone = buyerPhone,
                    qrSecretHash = hash,
                    status = "valid"
                )
                generatedTickets.add(ticket)
                eventDao.incrementTicketTypeSold(type.id, 1)
            }
        }

        ticketDao.insertTickets(generatedTickets)
        eventDao.incrementSoldCount(eventId, totalTicketsCount)

        // Save transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = buyerId,
                type = "purchase",
                amountCfa = totalAmount,
                provider = provider,
                providerTransactionId = txId,
                description = "Achat de $totalTicketsCount billet(s) pour ${event.title}"
            )
        )

        // 10% platform commission, 90% credited to artist wallet
        val artistPayout = (totalAmount * 0.90).toInt()
        val wallet = transactionDao.getArtistWallet(event.artistId)
        if (wallet != null) {
            val updated = wallet.copy(
                balanceCfa = wallet.balanceCfa + artistPayout,
                totalTicketsSold = wallet.totalTicketsSold + totalTicketsCount
            )
            transactionDao.insertOrUpdateWallet(updated)
        } else {
            transactionDao.insertOrUpdateWallet(
                ArtistWalletEntity(
                    artistId = event.artistId,
                    stageName = event.artistName,
                    balanceCfa = artistPayout,
                    totalTicketsSold = totalTicketsCount,
                    mobileMoneyPhone = "+221 77 000 00 00",
                    provider = "Wave"
                )
            )
        }

        PurchaseResult(true, txId, generatedTickets)
    }

    // Transfer ticket to a friend's phone number
    suspend fun transferTicket(ticketId: String, newPhone: String): Boolean = withContext(Dispatchers.IO) {
        val ticket = ticketDao.getTicketById(ticketId) ?: return@withContext false
        if (ticket.status != "valid") return@withContext false
        val updated = ticket.copy(
            buyerPhone = newPhone,
            status = "valid",
            transferredToPhone = newPhone
        )
        ticketDao.updateTicket(updated)
        true
    }

    // --- VIGILE / SCANNER MODE (B5) ---
    sealed class ScanResult {
        data class Valid(val ticket: TicketEntity, val message: String) : ScanResult()
        data class AlreadyScanned(val ticket: TicketEntity, val scannedAtText: String) : ScanResult()
        data class Invalid(val reason: String) : ScanResult()
    }

    suspend fun verifyAndScanTicket(scannedId: String, currentEventId: String?): ScanResult = withContext(Dispatchers.IO) {
        val cleanId = scannedId.trim()

        // Check local cache first (for offline resilience during the 2h event!)
        if (localScannedCache.containsKey(cleanId)) {
            val scannedTime = localScannedCache[cleanId] ?: System.currentTimeMillis()
            val ticket = ticketDao.getTicketById(cleanId)
            val timeStr = java.text.SimpleDateFormat("HH'h'mm", java.util.Locale.FRANCE).format(scannedTime)
            return@withContext if (ticket != null) {
                ScanResult.AlreadyScanned(ticket, "Déjà scanné à $timeStr (vérifié en cache local)")
            } else {
                ScanResult.Invalid("Ticket déjà scanné à $timeStr")
            }
        }

        val ticket = ticketDao.getTicketById(cleanId) ?: return@withContext ScanResult.Invalid("Code QR inconnu ou faux billet !")

        // Verify event match
        if (currentEventId != null && ticket.eventId != currentEventId) {
            return@withContext ScanResult.Invalid("Attention: Ce ticket est pour '${ticket.eventTitle}' et non cet événement !")
        }

        if (ticket.status == "scanned") {
            val time = ticket.scannedAtMillis ?: System.currentTimeMillis()
            val timeStr = java.text.SimpleDateFormat("HH'h'mm", java.util.Locale.FRANCE).format(time)
            return@withContext ScanResult.AlreadyScanned(ticket, "Déjà utilisé à $timeStr")
        }

        // Mark as scanned
        val now = System.currentTimeMillis()
        val updated = ticket.copy(status = "scanned", scannedAtMillis = now)
        ticketDao.updateTicket(updated)

        // Cache in local offline memory
        localScannedCache[cleanId] = now

        ScanResult.Valid(updated, "Billet ${ticket.ticketTypeName} vérifié avec succès !")
    }

    // --- TICKETS USER ---
    fun getTicketsForUser(userId: String, phone: String): Flow<List<TicketEntity>> =
        ticketDao.getTicketsForUser(userId, phone)

    // --- ARTIST WALLET & WITHDRAWALS ---
    fun getArtistWallet(artistId: String): Flow<ArtistWalletEntity?> =
        transactionDao.getArtistWalletFlow(artistId)

    suspend fun requestWithdrawal(artistId: String, amountCfa: Int, provider: String, phone: String): Boolean = withContext(Dispatchers.IO) {
        val wallet = transactionDao.getArtistWallet(artistId) ?: return@withContext false
        if (wallet.balanceCfa < amountCfa || amountCfa < 10000) return@withContext false

        val updated = wallet.copy(
            balanceCfa = wallet.balanceCfa - amountCfa,
            pendingWithdrawalCfa = wallet.pendingWithdrawalCfa + amountCfa
        )
        transactionDao.insertOrUpdateWallet(updated)

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = artistId,
                type = "payout",
                amountCfa = amountCfa,
                provider = provider,
                providerTransactionId = "WD-" + UUID.randomUUID().toString().take(6).uppercase(),
                description = "Demande de retrait vers $provider ($phone) en attente"
            )
        )
        true
    }

    // --- ADMIN BACK-OFFICE ---
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getTotalTicketsSold(): Flow<Int> = ticketDao.getTotalTicketsSoldCount()

    suspend fun moderateTrack(trackId: String, approved: Boolean) = withContext(Dispatchers.IO) {
        trackDao.updateStatus(trackId, if (approved) "approved" else "rejected")
    }

    suspend fun moderateEvent(eventId: String, approved: Boolean) = withContext(Dispatchers.IO) {
        eventDao.updateStatus(eventId, if (approved) "approved" else "rejected")
    }

    suspend fun toggleUserVerification(userId: String, isVerified: Boolean) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext
        userDao.updateUser(user.copy(isVerified = isVerified))
    }

    suspend fun findUserByPhone(phone: String): UserEntity? = userDao.getUserByPhone(phone)

    // --- PLAYLISTS ---
    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(title: String, description: String = ""): PlaylistEntity = withContext(Dispatchers.IO) {
        val playlist = PlaylistEntity(
            id = "pl_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description,
            createdAt = System.currentTimeMillis()
        )
        playlistDao.insertPlaylist(playlist)
        playlist
    }

    suspend fun deletePlaylist(playlistId: String) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylistCrossRefs(playlistId)
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: String) = withContext(Dispatchers.IO) {
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(playlistId, trackId))
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) = withContext(Dispatchers.IO) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>> =
        playlistDao.getTracksForPlaylist(playlistId)

    // --- NOTIFICATIONS ---
    fun getAllNotifications(): Flow<List<AppNotificationEntity>> = notificationDao.getAllNotifications()
    fun getUnreadNotificationCount(): Flow<Int> = notificationDao.getUnreadCount()

    suspend fun addNotification(title: String, message: String, type: String = "info", targetId: String? = null) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(
            AppNotificationEntity(
                title = title,
                message = message,
                type = type,
                targetId = targetId,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(id: String) = withContext(Dispatchers.IO) {
        notificationDao.deleteNotification(id)
    }

    // --- ARTIST BIOS ---
    fun getArtistBio(artistId: String): ArtistBio {
        return when (artistId) {
            "artist_awa" -> ArtistBio(
                id = "artist_awa",
                stageName = "Awa Ndar",
                realName = "Awa Sène",
                city = "Saint-Louis (Guet Ndar)",
                bio = "Née et bercée par les vagues et les pirogues de Guet Ndar à Saint-Louis, Awa Ndar mêle le chant traditionnel des femmes de pêcheurs à la douceur de la guitare acoustique et du xalam. Voix emblématique de la nouvelle vague fluviale sénégalaise, elle enchante les scènes du Quai et de l'Institut Français.",
                quote = "« Le fleuve et la mer chantent en nous à chaque marée. »",
                genres = listOf("Acoustic", "Folk Fluvial", "Mbalax Doux"),
                followersCount = 42800,
                monthlyListeners = 112000
            )
            "artist_ngaaka" -> ArtistBio(
                id = "artist_ngaaka",
                stageName = "Ngaaka Blindé",
                realName = "Baba Ndiaye",
                city = "Guédiawaye / Rufisque / Dakar",
                bio = "Pionnier incontournable du Rap Galsen percutant et des punchlines ciselées, Ngaaka Blindé fédère la jeunesse de la banlieue dakaroise jusqu'à Saint-Louis. Ses performances live électrisent les quais et festivals du pays avec une authenticité et une énergie inégalées.",
                quote = "« La rue nous a formés, le travail nous hisse au sommet. »",
                genres = listOf("Rap Galsen", "Trap Galsen", "Afrobeat"),
                followersCount = 185000,
                monthlyListeners = 340000
            )
            else -> ArtistBio(
                id = artistId,
                stageName = "Orchestre du Quai",
                realName = "Collectif Fluvial",
                city = "Saint-Louis, Sénégal",
                bio = "Ensemble musical fondé sur les quais de Saint-Louis réunissant les meilleurs maîtres du sabar, de la kora et des percussions pour des sessions acoustiques inoubliables.",
                quote = "« Le son du quai ne s'éteint jamais. »",
                genres = listOf("Mbalax", "Folk Traditionnel", "Afro-Jazz"),
                followersCount = 28000,
                monthlyListeners = 78000
            )
        }
    }
}
