package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.QuayGuetRepository
import com.example.player.PlaybackState
import com.example.player.QuayAudioPlayer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenNav {
    object Home : ScreenNav()
    object Library : ScreenNav()
    object Search : ScreenNav()
    object Profile : ScreenNav()
    object ProUpgrade : ScreenNav()
    object Events : ScreenNav()
    object ArtistSpace : ScreenNav()
    object Scanner : ScreenNav()
    object Admin : ScreenNav()
    object TopCharts : ScreenNav()
    data class EventDetail(val eventId: String) : ScreenNav()
    data class ArtistProfile(val artistId: String) : ScreenNav()
}

class QuayGuetViewModel(application: Application) : AndroidViewModel(application) {
    val repository = QuayGuetRepository(application)
    val audioPlayer = QuayAudioPlayer(application, repository)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<ScreenNav>(ScreenNav.Home)
    val currentScreen: StateFlow<ScreenNav> = _currentScreen.asStateFlow()

    // Full audio player modal
    private val _isFullPlayerExpanded = MutableStateFlow(false)
    val isFullPlayerExpanded: StateFlow<Boolean> = _isFullPlayerExpanded.asStateFlow()

    // Version PRO state
    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _isProModalOpen = MutableStateFlow(false)
    val isProModalOpen: StateFlow<Boolean> = _isProModalOpen.asStateFlow()

    // Playback state
    val playbackState: StateFlow<PlaybackState> = audioPlayer.playbackState

    // Current logged-in user (default Modou)
    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = "user_modou",
            phone = "+221 77 123 45 67",
            username = "modou_rufisque",
            role = "listener",
            isVerified = false
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Data streams
    val allTracks: StateFlow<List<TrackEntity>> = repository.getAllTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val top10Tracks: StateFlow<List<TrackEntity>> = repository.getTop10Tracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTracks: StateFlow<List<TrackEntity>> = repository.getRecentTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dakarTracks: StateFlow<List<TrackEntity>> = repository.getTracksByCity("Dakar")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val saintLouisTracks: StateFlow<List<TrackEntity>> = repository.getTracksByCity("Saint-Louis")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedTracks: StateFlow<List<TrackEntity>> = repository.getDownloadedTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<EventEntity>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTickets: StateFlow<List<TicketEntity>> = _currentUser.flatMapLatest { user ->
        repository.getTicketsForUser(user.id, user.phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artistWallet: StateFlow<ArtistWalletEntity?> = _currentUser.flatMapLatest { user ->
        repository.getArtistWallet(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTracks: StateFlow<List<TrackEntity>> = repository.getPendingTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingEvents: StateFlow<List<EventEntity>> = repository.getPendingEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Playlists streams & state
    val allPlaylists: StateFlow<List<PlaylistEntity>> = repository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val selectedPlaylist: StateFlow<PlaylistEntity?> = _selectedPlaylist.asStateFlow()

    val selectedPlaylistTracks: StateFlow<List<TrackEntity>> = _selectedPlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getTracksForPlaylist(pl.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _trackToAddToPlaylist = MutableStateFlow<TrackEntity?>(null)
    val trackToAddToPlaylist: StateFlow<TrackEntity?> = _trackToAddToPlaylist.asStateFlow()

    // Notifications streams & state
    val allNotifications: StateFlow<List<AppNotificationEntity>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isNotificationDialogOpen = MutableStateFlow(false)
    val isNotificationDialogOpen: StateFlow<Boolean> = _isNotificationDialogOpen.asStateFlow()

    // Artist presentation state
    private val _selectedArtistBio = MutableStateFlow<ArtistBio?>(null)
    val selectedArtistBio: StateFlow<ArtistBio?> = _selectedArtistBio.asStateFlow()

    // Comments Modal State
    private val _activeCommentsTrack = MutableStateFlow<TrackEntity?>(null)
    val activeCommentsTrack: StateFlow<TrackEntity?> = _activeCommentsTrack.asStateFlow()

    val currentTrackComments: StateFlow<List<TrackCommentEntity>> = _activeCommentsTrack.flatMapLatest { track ->
        if (track != null) repository.getCommentsForTrack(track.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tipping Modal State
    private val _tippingTrack = MutableStateFlow<TrackEntity?>(null)
    val tippingTrack: StateFlow<TrackEntity?> = _tippingTrack.asStateFlow()

    // Equalizer Sheet State
    private val _isEqualizerOpen = MutableStateFlow(false)
    val isEqualizerOpen: StateFlow<Boolean> = _isEqualizerOpen.asStateFlow()

    // Top Charts Period & State
    private val _selectedChartPeriod = MutableStateFlow("top20") // "top20", "24h", "saint_louis"
    val selectedChartPeriod: StateFlow<String> = _selectedChartPeriod.asStateFlow()

    val dynamicCharts: StateFlow<List<ChartRankItem>> = combine(allTracks, _selectedChartPeriod) { tracks, period ->
        val sorted = when (period) {
            "24h" -> tracks.sortedByDescending { it.plays * 3 + it.likes * 5 }
            "saint_louis" -> tracks.filter { it.city.contains("Saint-Louis", ignoreCase = true) || it.artistName.contains("Ndar", ignoreCase = true) }
                .ifEmpty { tracks }
                .sortedByDescending { it.plays + it.likes }
            else -> tracks.sortedByDescending { it.plays }
        }
        sorted.mapIndexed { index, track ->
            val rank = index + 1
            val prevRank = when (rank) {
                1 -> 2
                2 -> 1
                3 -> 3
                4 -> 6
                5 -> 4
                else -> if (rank % 2 == 0) rank + 1 else rank - 1
            }
            val trend = when {
                rank < prevRank -> "UP"
                rank > prevRank -> "DOWN"
                rank == 5 -> "NEW"
                else -> "SAME"
            }
            ChartRankItem(
                track = track,
                rank = rank,
                previousRank = prevRank,
                trend = trend,
                weeklyStreams = (track.plays * 1.8).toInt() + (index * 420)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Checkout Sheet State
    private val _checkoutEvent = MutableStateFlow<EventEntity?>(null)
    val checkoutEvent: StateFlow<EventEntity?> = _checkoutEvent.asStateFlow()

    private val _ticketQuantities = MutableStateFlow<Map<TicketTypeEntity, Int>>(emptyMap())
    val ticketQuantities: StateFlow<Map<TicketTypeEntity, Int>> = _ticketQuantities.asStateFlow()

    private val _selectedPaymentProvider = MutableStateFlow("Wave")
    val selectedPaymentProvider: StateFlow<String> = _selectedPaymentProvider.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentStep>(PaymentStep.IDLE)
    val paymentState: StateFlow<PaymentStep> = _paymentState.asStateFlow()

    // Scanner state
    private val _scannerEventId = MutableStateFlow<String?>("event_guet_ndar")
    val scannerEventId: StateFlow<String?> = _scannerEventId.asStateFlow()

    private val _lastScanResult = MutableStateFlow<QuayGuetRepository.ScanResult?>(null)
    val lastScanResult: StateFlow<QuayGuetRepository.ScanResult?> = _lastScanResult.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initSeedDataIfNeeded()
            // Set initial playlist from seed
            repository.getAllTracks().collectLatest { tracks ->
                if (tracks.isNotEmpty() && audioPlayer.playbackState.value.currentTrack == null) {
                    audioPlayer.setPlaylist(tracks, tracks.first())
                }
            }
        }
    }

    fun navigateTo(screen: ScreenNav) {
        _currentScreen.value = screen
    }

    fun openFullPlayer() {
        _isFullPlayerExpanded.value = true
    }

    fun closeFullPlayer() {
        _isFullPlayerExpanded.value = false
    }

    fun openProModal() {
        _isProModalOpen.value = true
    }

    fun closeProModal() {
        _isProModalOpen.value = false
    }

    fun subscribeToPro(plan: String = "monthly", provider: String = "Wave") {
        viewModelScope.launch {
            _isProUser.value = true
            _isProModalOpen.value = false
            audioPlayer.setAudioQuality("HIGH")
            val price = if (plan == "annual") "19 000 FCFA/an" else "2 000 FCFA/mois"
            _userMessage.value = "⭐ Félicitations ! Votre abonnement QUAY PRO ($price via $provider) est activé avec succès !"
        }
    }

    fun toggleProStatus() {
        _isProUser.value = !_isProUser.value
        if (_isProUser.value) {
            audioPlayer.setAudioQuality("HIGH")
            _userMessage.value = "⭐ Mode QUAY PRO activé ! Audio FLAC 320k & Coupe-file concerts disponibles."
        } else {
            _userMessage.value = "Mode Standard rétabli."
        }
    }

    fun switchUser(role: String) {
        viewModelScope.launch {
            val user = when (role) {
                "artist" -> UserEntity(
                    id = "artist_awa",
                    phone = "+221 78 456 78 90",
                    username = "awa_ndar",
                    role = "artist",
                    isVerified = true
                )
                "vigil" -> UserEntity(
                    id = "user_vigile",
                    phone = "+221 76 999 00 11",
                    username = "vigile_quai",
                    role = "vigil",
                    isVerified = true
                )
                "admin" -> UserEntity(
                    id = "user_admin",
                    phone = "+221 77 000 00 00",
                    username = "admin_quay221",
                    role = "admin",
                    isVerified = true
                )
                else -> UserEntity(
                    id = "user_modou",
                    phone = "+221 77 123 45 67",
                    username = "modou_rufisque",
                    role = "listener",
                    isVerified = false
                )
            }
            _currentUser.value = user
            _userMessage.value = "Connecté en tant que: @${user.username} (${user.role.uppercase()})"
        }
    }

    fun playTrack(track: TrackEntity) {
        audioPlayer.playTrack(track)
    }

    fun toggleDownload(track: TrackEntity) {
        viewModelScope.launch {
            val downloaded = repository.toggleDownloadTrack(track.id, track.isDownloaded)
            _userMessage.value = if (downloaded) {
                "Téléchargé, disponible sans connexion !"
            } else {
                "Supprimé des téléchargements hors-ligne."
            }
        }
    }

    fun clearDownloads() {
        viewModelScope.launch {
            repository.clearAllDownloads()
            _userMessage.value = "Tous les téléchargements ont été purgés."
        }
    }

    // Checkout Flow
    fun startCheckout(event: EventEntity) {
        viewModelScope.launch {
            _checkoutEvent.value = event
            val types = repository.getTicketTypesForEvent(event.id).firstOrNull() ?: emptyList()
            val initialMap = types.associateWith { if (it == types.firstOrNull()) 1 else 0 }
            _ticketQuantities.value = initialMap
            _paymentState.value = PaymentStep.SELECTING
        }
    }

    fun updateTicketQuantity(type: TicketTypeEntity, delta: Int) {
        val current = _ticketQuantities.value.toMutableMap()
        val currentQty = current[type] ?: 0
        val newQty = (currentQty + delta).coerceIn(0, 5)
        current[type] = newQty
        _ticketQuantities.value = current
    }

    fun selectPaymentProvider(provider: String) {
        _selectedPaymentProvider.value = provider
    }

    fun confirmPurchase() {
        val event = _checkoutEvent.value ?: return
        val map = _ticketQuantities.value.filterValues { it > 0 }
        if (map.isEmpty()) {
            _userMessage.value = "Veuillez sélectionner au moins un ticket."
            return
        }

        viewModelScope.launch {
            _paymentState.value = PaymentStep.PROCESSING
            kotlinx.coroutines.delay(1600) // Simulate PayDunya Wave / OM USSD push
            val res = repository.purchaseTickets(
                buyerId = _currentUser.value.id,
                buyerPhone = _currentUser.value.phone,
                eventId = event.id,
                selectedTickets = map,
                provider = _selectedPaymentProvider.value
            )
            if (res.success) {
                _paymentState.value = PaymentStep.SUCCESS(res.tickets)
                _userMessage.value = "${res.tickets.size} ticket(s) achetés avec succès !"
            } else {
                _paymentState.value = PaymentStep.ERROR(res.errorMessage ?: "Erreur de paiement")
            }
        }
    }

    fun dismissCheckout() {
        _checkoutEvent.value = null
        _paymentState.value = PaymentStep.IDLE
    }

    // Transfer ticket
    fun transferTicket(ticketId: String, recipientPhone: String) {
        viewModelScope.launch {
            val ok = repository.transferTicket(ticketId, recipientPhone)
            _userMessage.value = if (ok) {
                "Ticket transféré avec succès au $recipientPhone !"
            } else {
                "Impossible de transférer ce ticket."
            }
        }
    }

    // Vigile scanner
    fun scanTicket(ticketId: String) {
        viewModelScope.launch {
            val result = repository.verifyAndScanTicket(ticketId, _scannerEventId.value)
            _lastScanResult.value = result
        }
    }

    fun clearScanResult() {
        _lastScanResult.value = null
    }

    fun setScannerEvent(eventId: String) {
        _scannerEventId.value = eventId
    }

    // Artist Actions
    fun uploadTrack(title: String, feat: String, genre: String, city: String, lyrics: String) {
        viewModelScope.launch {
            repository.uploadTrack(
                artistId = _currentUser.value.id,
                artistName = _currentUser.value.username,
                title = title,
                feat = feat,
                genre = genre,
                city = city,
                lyrics = lyrics
            )
            _userMessage.value = "Morceau '$title' uploadé et encodé en 128k & 320k !"
        }
    }

    fun createEvent(
        title: String,
        venue: String,
        city: String,
        dateTimeText: String,
        description: String,
        tickets: List<Pair<String, Pair<Int, Int>>>,
        linkedTrackIds: List<String>
    ) {
        viewModelScope.launch {
            val evt = repository.createEventWithTickets(
                artistId = _currentUser.value.id,
                artistName = _currentUser.value.username,
                title = title,
                venueName = venue,
                city = city,
                dateTimeText = dateTimeText,
                description = description,
                ticketTypes = tickets,
                linkedTrackIds = linkedTrackIds
            )
            _userMessage.value = "Événement '${evt.title}' créé et lié à ${linkedTrackIds.size} morceau(x) !"
        }
    }

    fun requestWithdrawal(amountCfa: Int, provider: String, phone: String) {
        viewModelScope.launch {
            val ok = repository.requestWithdrawal(_currentUser.value.id, amountCfa, provider, phone)
            _userMessage.value = if (ok) {
                "Demande de retrait de $amountCfa FCFA soumise avec succès !"
            } else {
                "Solde insuffisant ou montant inférieur à 10 000 FCFA."
            }
        }
    }

    // Admin Actions
    fun moderateTrack(trackId: String, approved: Boolean) {
        viewModelScope.launch {
            repository.moderateTrack(trackId, approved)
            _userMessage.value = if (approved) "Morceau validé !" else "Morceau rejeté."
        }
    }

    fun moderateEvent(eventId: String, approved: Boolean) {
        viewModelScope.launch {
            repository.moderateEvent(eventId, approved)
            _userMessage.value = if (approved) "Événement publié au Quai !" else "Événement rejeté."
        }
    }

    fun toggleUserVerification(userId: String, isVerified: Boolean) {
        viewModelScope.launch {
            repository.toggleUserVerification(userId, isVerified)
            _userMessage.value = "Statut vérifié mis à jour."
        }
    }

    // --- PLAYLIST ACTIONS ---
    fun createPlaylist(title: String, description: String = "") {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            val pl = repository.createPlaylist(title.trim(), description.trim())
            _userMessage.value = "Playlist « ${pl.title} » créée avec succès !"
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = null
            }
            _userMessage.value = "Playlist supprimée."
        }
    }

    fun selectPlaylist(playlist: PlaylistEntity?) {
        _selectedPlaylist.value = playlist
    }

    fun playPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            val tracks = repository.getTracksForPlaylist(playlist.id).firstOrNull() ?: emptyList()
            if (tracks.isNotEmpty()) {
                audioPlayer.setPlaylist(tracks, tracks.first())
                _userMessage.value = "Lecture de « ${playlist.title} » (${tracks.size} titres)"
            } else {
                _userMessage.value = "Cette playlist est vide. Ajoutez-y des sons !"
            }
        }
    }

    fun openAddToPlaylist(track: TrackEntity) {
        _trackToAddToPlaylist.value = track
    }

    fun closeAddToPlaylist() {
        _trackToAddToPlaylist.value = null
    }

    fun addTrackToPlaylist(playlistId: String, track: TrackEntity) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, track.id)
            _trackToAddToPlaylist.value = null
            _userMessage.value = "« ${track.title} » ajouté à la playlist !"
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, trackId)
            _userMessage.value = "Morceau retiré de la playlist."
        }
    }

    // --- NOTIFICATION ACTIONS ---
    fun openNotificationDialog() {
        _isNotificationDialogOpen.value = true
    }

    fun closeNotificationDialog() {
        _isNotificationDialogOpen.value = false
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun sendConcertReminder(event: EventEntity) {
        viewModelScope.launch {
            val title = "🔔 Rappel Concert : ${event.title}"
            val message = "Concert prévu au ${event.venueName} (${event.dateTimeText}) ! Votre pass QR est prêt."
            repository.addNotification(title, message, "concert_reminder", event.id)
            com.example.util.NotificationHelper.showSystemNotification(getApplication(), 101, title, message)
            _userMessage.value = "Rappel activé pour le concert « ${event.title} » !"
        }
    }

    // --- ARTIST BIO ACTIONS ---
    fun openArtistBio(artistId: String) {
        _selectedArtistBio.value = repository.getArtistBio(artistId)
    }

    fun closeArtistBio() {
        _selectedArtistBio.value = null
    }

    // --- PLAYBACK DELEGATES FOR SYSTEM CONTROLS ---
    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun nextTrack() {
        audioPlayer.next()
    }

    fun previousTrack() {
        audioPlayer.previous()
    }

    fun seekTo(seconds: Int) {
        audioPlayer.seekTo(seconds)
    }

    // --- EQUALIZER ACTIONS ---
    fun openEqualizer() {
        _isEqualizerOpen.value = true
    }

    fun closeEqualizer() {
        _isEqualizerOpen.value = false
    }

    fun setEqualizerProfile(profile: EqualizerProfile) {
        audioPlayer.setEqualizerProfile(profile)
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        audioPlayer.setBandGain(bandIndex, gainDb)
    }

    fun resetEqualizerBands() {
        audioPlayer.resetEqualizerBands()
        _userMessage.value = "Égaliseur réinitialisé (0 dB)"
    }

    fun setBassBoost(percent: Int) {
        audioPlayer.setBassBoost(percent)
    }

    fun toggleEqualizer() {
        audioPlayer.toggleEqualizer()
    }

    // --- COMMENTS ACTIONS (AUDIOMACK / BOOMPLAY STYLE) ---
    fun openComments(track: TrackEntity) {
        _activeCommentsTrack.value = track
    }

    fun closeComments() {
        _activeCommentsTrack.value = null
    }

    fun postComment(text: String, timestampSec: Int = 0, timestampText: String = "") {
        val track = _activeCommentsTrack.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(
                trackId = track.id,
                authorName = _currentUser.value.username,
                text = text.trim(),
                timestampSec = timestampSec,
                timestampText = timestampText
            )
            _userMessage.value = "Commentaire publié sur « ${track.title} » !"
        }
    }

    fun likeComment(commentId: String) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    // --- ARTIST TIPPING / SUPPORTERS (BOOMPLAY / AUDIOMACK TIPPING) ---
    fun openTipModal(track: TrackEntity) {
        _tippingTrack.value = track
    }

    fun closeTipModal() {
        _tippingTrack.value = null
    }

    fun sendTip(amountCfa: Int, provider: String, message: String) {
        val track = _tippingTrack.value ?: return
        viewModelScope.launch {
            val success = repository.sendTipToArtist(
                artistId = track.artistId,
                artistName = track.artistName,
                trackId = track.id,
                trackTitle = track.title,
                senderName = _currentUser.value.username,
                amountCfa = amountCfa,
                provider = provider,
                message = message
            )
            _tippingTrack.value = null
            if (success) {
                _userMessage.value = "❤️ Pourboire de $amountCfa FCFA ($provider) envoyé avec succès à ${track.artistName} !"
            }
        }
    }

    // --- CHARTS PERIOD ---
    fun setChartPeriod(period: String) {
        _selectedChartPeriod.value = period
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }
}

sealed class PaymentStep {
    object IDLE : PaymentStep()
    object SELECTING : PaymentStep()
    object PROCESSING : PaymentStep()
    data class SUCCESS(val tickets: List<TicketEntity>) : PaymentStep()
    data class ERROR(val message: String) : PaymentStep()
}
