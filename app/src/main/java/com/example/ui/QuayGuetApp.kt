package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventEntity
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.QuayGuetViewModel
import com.example.viewmodel.ScreenNav

@Composable
fun QuayGuetApp(
    viewModel: QuayGuetViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isFullPlayerExpanded by viewModel.isFullPlayerExpanded.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val allTracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val top10Tracks by viewModel.top10Tracks.collectAsStateWithLifecycle()
    val recentTracks by viewModel.recentTracks.collectAsStateWithLifecycle()
    val dakarTracks by viewModel.dakarTracks.collectAsStateWithLifecycle()
    val saintLouisTracks by viewModel.saintLouisTracks.collectAsStateWithLifecycle()
    val downloadedTracks by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val userTickets by viewModel.userTickets.collectAsStateWithLifecycle()
    val artistWallet by viewModel.artistWallet.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val pendingTracks by viewModel.pendingTracks.collectAsStateWithLifecycle()
    val pendingEvents by viewModel.pendingEvents.collectAsStateWithLifecycle()

    val checkoutEvent by viewModel.checkoutEvent.collectAsStateWithLifecycle()
    val ticketQuantities by viewModel.ticketQuantities.collectAsStateWithLifecycle()
    val selectedPaymentProvider by viewModel.selectedPaymentProvider.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()

    val scannerEventId by viewModel.scannerEventId.collectAsStateWithLifecycle()
    val lastScanResult by viewModel.lastScanResult.collectAsStateWithLifecycle()

    val isProUser by viewModel.isProUser.collectAsStateWithLifecycle()

    val activeCommentsTrack by viewModel.activeCommentsTrack.collectAsStateWithLifecycle()
    val currentComments by viewModel.currentTrackComments.collectAsStateWithLifecycle()
    val tippingTrack by viewModel.tippingTrack.collectAsStateWithLifecycle()
    val isEqualizerOpen by viewModel.isEqualizerOpen.collectAsStateWithLifecycle()
    val dynamicCharts by viewModel.dynamicCharts.collectAsStateWithLifecycle()
    val selectedChartPeriod by viewModel.selectedChartPeriod.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuayTopBar(
                currentUser = currentUser,
                isOfflineMode = playbackState.isOfflineModeActive,
                isProUser = isProUser,
                onToggleOffline = { viewModel.audioPlayer.toggleOfflineMode() },
                onOpenPro = { viewModel.navigateTo(ScreenNav.ProUpgrade) },
                onSwitchUserRole = { role ->
                    viewModel.switchUser(role)
                    when (role) {
                        "artist" -> viewModel.navigateTo(ScreenNav.ArtistSpace)
                        "vigil" -> viewModel.navigateTo(ScreenNav.Scanner)
                        "admin" -> viewModel.navigateTo(ScreenNav.Admin)
                        else -> viewModel.navigateTo(ScreenNav.Home)
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Persistent MiniPlayer (with Contextual Live Event CTA!)
                if (playbackState.currentTrack != null) {
                    PersistentMiniPlayer(
                        playbackState = playbackState,
                        onTogglePlayPause = { viewModel.audioPlayer.togglePlayPause() },
                        onNext = { viewModel.audioPlayer.next() },
                        onOpenFullPlayer = { viewModel.openFullPlayer() },
                        onLiveEventClick = { event ->
                            viewModel.navigateTo(ScreenNav.EventDetail(event.id))
                        }
                    )
                }

                // Bottom Navigation Bar with the 4 primary sections
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is ScreenNav.Home,
                        onClick = { viewModel.navigateTo(ScreenNav.Home) },
                        icon = {
                            Icon(
                                if (currentScreen is ScreenNav.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Le Quai"
                            )
                        },
                        label = { Text("Le Quai", fontSize = 11.sp, fontWeight = if (currentScreen is ScreenNav.Home) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenNav.TopCharts,
                        onClick = { viewModel.navigateTo(ScreenNav.TopCharts) },
                        icon = {
                            Icon(
                                if (currentScreen is ScreenNav.TopCharts) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
                                contentDescription = "Charts"
                            )
                        },
                        label = { Text("Charts", fontSize = 11.sp, fontWeight = if (currentScreen is ScreenNav.TopCharts) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_item_charts")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenNav.Library,
                        onClick = { viewModel.navigateTo(ScreenNav.Library) },
                        icon = {
                            Icon(
                                if (currentScreen is ScreenNav.Library) Icons.Filled.FolderSpecial else Icons.Outlined.FolderSpecial,
                                contentDescription = "Bibliothèque"
                            )
                        },
                        label = { Text("Bibliothèque", fontSize = 11.sp, fontWeight = if (currentScreen is ScreenNav.Library) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_item_library")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenNav.Search,
                        onClick = { viewModel.navigateTo(ScreenNav.Search) },
                        icon = {
                            Icon(
                                if (currentScreen is ScreenNav.Search) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Recherche"
                            )
                        },
                        label = { Text("Recherche", fontSize = 11.sp, fontWeight = if (currentScreen is ScreenNav.Search) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_item_search")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenNav.Profile,
                        onClick = { viewModel.navigateTo(ScreenNav.Profile) },
                        icon = {
                            Icon(
                                if (currentScreen is ScreenNav.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profil"
                            )
                        },
                        label = { Text("Profil", fontSize = 11.sp, fontWeight = if (currentScreen is ScreenNav.Profile) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is ScreenNav.Home -> {
                    HomeScreen(
                        playbackState = playbackState,
                        allTracks = allTracks,
                        top10Tracks = top10Tracks,
                        recentTracks = recentTracks,
                        dakarTracks = dakarTracks,
                        saintLouisTracks = saintLouisTracks,
                        events = allEvents,
                        isProUser = isProUser,
                        onPlayTrack = { viewModel.playTrack(it) },
                        onDownloadTrack = { viewModel.toggleDownload(it) },
                        onSelectEvent = { viewModel.navigateTo(ScreenNav.EventDetail(it.id)) },
                        onNavigateToEvents = { viewModel.navigateTo(ScreenNav.Events) },
                        onNavigateToPro = { viewModel.navigateTo(ScreenNav.ProUpgrade) },
                        onNavigateToCharts = { viewModel.navigateTo(ScreenNav.TopCharts) }
                    )
                }

                is ScreenNav.Search -> {
                    SearchScreen(
                        allTracks = allTracks,
                        playbackState = playbackState,
                        onPlayTrack = { viewModel.playTrack(it) },
                        onDownloadTrack = { viewModel.toggleDownload(it) }
                    )
                }

                is ScreenNav.Events -> {
                    EventsCatalogScreen(
                        events = allEvents,
                        onSelectEvent = { viewModel.navigateTo(ScreenNav.EventDetail(it.id)) },
                        onBackClick = { viewModel.navigateTo(ScreenNav.Home) }
                    )
                }

                is ScreenNav.TopCharts -> {
                    TopChartsScreen(
                        charts = dynamicCharts,
                        selectedPeriod = selectedChartPeriod,
                        onPeriodSelect = { viewModel.setChartPeriod(it) },
                        onTrackClick = { viewModel.playTrack(it) },
                        onAddToPlaylist = { viewModel.openAddToPlaylist(it) },
                        onOpenComments = { viewModel.openComments(it) },
                        onTipArtist = { viewModel.openTipModal(it) },
                        onShareTrack = { track ->
                            com.example.util.ShareHelper.shareTrackOnWhatsApp(
                                context = context,
                                title = track.title,
                                artist = track.artistName,
                                link = "https://quayguett221.sn/track/${track.id}"
                            )
                        },
                        onBackClick = { viewModel.navigateTo(ScreenNav.Home) }
                    )
                }

                is ScreenNav.ProUpgrade -> {
                    QuayProScreen(
                        isProUser = isProUser,
                        onSubscribe = { plan, provider -> viewModel.subscribeToPro(plan, provider) },
                        onCancelSubscription = { viewModel.toggleProStatus() },
                        onBackClick = { viewModel.navigateTo(ScreenNav.Home) }
                    )
                }

                is ScreenNav.Profile -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        playbackState = playbackState,
                        userTickets = userTickets,
                        downloadedTracksCount = downloadedTracks.size,
                        isProUser = isProUser,
                        onToggleOfflineMode = { viewModel.audioPlayer.toggleOfflineMode() },
                        onChangeQuality = { viewModel.audioPlayer.setAudioQuality(it) },
                        onSwitchUserRole = { role ->
                            viewModel.switchUser(role)
                        },
                        onNavigateToEvents = { viewModel.navigateTo(ScreenNav.Events) },
                        onNavigateToArtistSpace = { viewModel.navigateTo(ScreenNav.ArtistSpace) },
                        onNavigateToScanner = { viewModel.navigateTo(ScreenNav.Scanner) },
                        onNavigateToAdmin = { viewModel.navigateTo(ScreenNav.Admin) },
                        onNavigateToLibrary = { viewModel.navigateTo(ScreenNav.Library) },
                        onNavigateToPro = { viewModel.navigateTo(ScreenNav.ProUpgrade) },
                        onTogglePro = { viewModel.toggleProStatus() }
                    )
                }

                is ScreenNav.Library -> {
                    LibraryScreen(
                        tickets = userTickets,
                        downloadedTracks = downloadedTracks,
                        favoriteTracks = allTracks.take(4),
                        onPlayTrack = { viewModel.playTrack(it) },
                        onDeleteDownload = { viewModel.toggleDownload(it) },
                        onPurgeAllDownloads = { viewModel.clearDownloads() },
                        onTransferTicket = { ticketId, phone -> viewModel.transferTicket(ticketId, phone) },
                        onNavigateToEvents = { viewModel.navigateTo(ScreenNav.Events) }
                    )
                }

                is ScreenNav.ArtistSpace -> {
                    ArtistSpaceScreen(
                        wallet = artistWallet,
                        myTracks = allTracks.filter { it.artistId == currentUser.id || it.artistId == "artist_awa" },
                        myEvents = allEvents.filter { it.artistId == currentUser.id || it.artistId == "artist_awa" },
                        onUploadTrack = { title, feat, genre, city, lyrics ->
                            viewModel.uploadTrack(title, feat, genre, city, lyrics)
                        },
                        onCreateEvent = { title, venue, city, dateTimeText, description, tickets, linkedTrackIds ->
                            viewModel.createEvent(title, venue, city, dateTimeText, description, tickets, linkedTrackIds)
                        },
                        onRequestWithdrawal = { amount, provider, phone ->
                            viewModel.requestWithdrawal(amount, provider, phone)
                        }
                    )
                }

                is ScreenNav.Scanner -> {
                    ScannerScreen(
                        events = allEvents,
                        selectedEventId = scannerEventId,
                        allTickets = userTickets,
                        lastScanResult = lastScanResult,
                        onSelectEvent = { viewModel.setScannerEvent(it) },
                        onScanCode = { viewModel.scanTicket(it) },
                        onClearScanResult = { viewModel.clearScanResult() }
                    )
                }

                is ScreenNav.Admin -> {
                    AdminScreen(
                        users = allUsers,
                        pendingTracks = pendingTracks,
                        pendingEvents = pendingEvents,
                        transactions = allTransactions,
                        onModerateTrack = { trackId, approved -> viewModel.moderateTrack(trackId, approved) },
                        onModerateEvent = { eventId, approved -> viewModel.moderateEvent(eventId, approved) },
                        onToggleUserVerified = { userId, isVerified -> viewModel.toggleUserVerification(userId, isVerified) }
                    )
                }

                is ScreenNav.EventDetail -> {
                    val event = allEvents.find { it.id == screen.eventId }
                    if (event != null) {
                        var ticketTypes by remember(event.id) { mutableStateOf<List<com.example.data.model.TicketTypeEntity>>(emptyList()) }
                        var linkedTracks by remember(event.id) { mutableStateOf<List<com.example.data.model.TrackEntity>>(emptyList()) }

                        LaunchedEffect(event.id) {
                            viewModel.repository.getTicketTypesForEvent(event.id).collect {
                                ticketTypes = it
                            }
                        }
                        LaunchedEffect(event.id) {
                            viewModel.repository.getTracksForEvent(event.id).collect {
                                linkedTracks = it
                            }
                        }

                        EventDetailScreen(
                            event = event,
                            ticketTypes = ticketTypes,
                            linkedTracks = linkedTracks,
                            selectedQuantities = ticketQuantities,
                            onUpdateQuantity = { type, delta -> viewModel.updateTicketQuantity(type, delta) },
                            onBuyTicketsClick = { viewModel.startCheckout(event) },
                            onPlayTrack = { viewModel.playTrack(it) },
                            onBackClick = { viewModel.navigateTo(ScreenNav.Home) }
                        )
                    }
                }

                is ScreenNav.ArtistProfile -> {
                    viewModel.navigateTo(ScreenNav.Home)
                }
            }
        }
    }

    // Modal Checkout Bottom Sheet (Module B3: PayDunya, Wave, Orange Money)
    if (checkoutEvent != null) {
        CheckoutBottomSheet(
            event = checkoutEvent!!,
            selectedQuantities = ticketQuantities,
            selectedProvider = selectedPaymentProvider,
            paymentState = paymentState,
            buyerPhone = currentUser.phone,
            onSelectProvider = { viewModel.selectPaymentProvider(it) },
            onConfirmPurchase = { viewModel.confirmPurchase() },
            onDismiss = { viewModel.dismissCheckout() },
            onViewMyTickets = {
                viewModel.dismissCheckout()
                viewModel.navigateTo(ScreenNav.Library)
            }
        )
    }

    // Full Player Modal Sheet (Module A3: Seekbar, Audio Quality 128k/320k/FLAC, Contextual Live CTA)
    if (isFullPlayerExpanded && playbackState.currentTrack != null) {
        FullPlayerSheet(
            playbackState = playbackState,
            isProUser = isProUser,
            onDismiss = { viewModel.closeFullPlayer() },
            onTogglePlayPause = { viewModel.audioPlayer.togglePlayPause() },
            onNext = { viewModel.audioPlayer.next() },
            onPrevious = { viewModel.audioPlayer.previous() },
            onSeekTo = { viewModel.audioPlayer.seekTo(it) },
            onToggleShuffle = { viewModel.audioPlayer.toggleShuffle() },
            onToggleRepeat = { viewModel.audioPlayer.toggleRepeat() },
            onChangeQuality = { viewModel.audioPlayer.setAudioQuality(it) },
            onDownloadClick = { viewModel.toggleDownload(it) },
            onLiveEventClick = { event ->
                viewModel.navigateTo(ScreenNav.EventDetail(event.id))
            },
            onOpenComments = { viewModel.openComments(it) },
            onOpenTipModal = { viewModel.openTipModal(it) },
            onOpenEqualizer = { viewModel.openEqualizer() }
        )
    }

    // Modal Espace Commentaires (Audiomack / Boomplay Community Feature)
    if (activeCommentsTrack != null) {
        TrackCommentsSheet(
            track = activeCommentsTrack!!,
            comments = currentComments,
            currentTrackPositionSec = playbackState.currentPositionSec,
            onDismiss = { viewModel.closeComments() },
            onPostComment = { text, sec, txt -> viewModel.postComment(text, sec, txt) },
            onLikeComment = { viewModel.likeComment(it) }
        )
    }

    // Modal Pourboire & Soutien Artiste (Wave / Orange Money)
    if (tippingTrack != null) {
        TipArtistDialog(
            track = tippingTrack!!,
            onDismiss = { viewModel.closeTipModal() },
            onSendTip = { amount, provider, msg -> viewModel.sendTip(amount, provider, msg) }
        )
    }

    // Modal Égaliseur Audio Graphique & Bass Boost (Boomplay Style)
    if (isEqualizerOpen) {
        AudioEqualizerSheet(
            isEqualizerEnabled = playbackState.isEqualizerEnabled,
            currentProfile = playbackState.equalizerProfile,
            bassBoostPercent = playbackState.bassBoostPercent,
            onDismiss = { viewModel.closeEqualizer() },
            onToggleEqualizer = { viewModel.toggleEqualizer() },
            onSelectProfile = { viewModel.setEqualizerProfile(it) },
            onBandGainChange = { bandIndex, gainDb -> viewModel.setBandGain(bandIndex, gainDb) },
            onResetBands = { viewModel.resetEqualizerBands() },
            onBassBoostChange = { viewModel.setBassBoost(it) }
        )
    }
}
