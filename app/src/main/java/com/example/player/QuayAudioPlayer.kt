package com.example.player

import android.content.Context
import com.example.data.model.EventEntity
import com.example.data.model.TrackEntity
import com.example.data.repository.QuayGuetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaybackState(
    val currentTrack: TrackEntity? = null,
    val linkedEvent: EventEntity? = null,
    val isPlaying: Boolean = false,
    val currentPositionSec: Int = 0,
    val durationSec: Int = 0,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val quality: String = "HIGH", // "LOW" 128kbps or "HIGH" 320kbps
    val isOfflineModeActive: Boolean = false
)

class QuayAudioPlayer(
    private val context: Context,
    private val repository: QuayGuetRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var playlist: List<TrackEntity> = emptyList()
    private var currentIndex: Int = -1
    private var playbackJob: Job? = null

    fun setPlaylist(tracks: List<TrackEntity>, startTrack: TrackEntity? = null) {
        playlist = tracks
        val target = startTrack ?: tracks.firstOrNull()
        if (target != null) {
            playTrack(target)
        }
    }

    fun playTrack(track: TrackEntity) {
        playbackJob?.cancel()
        currentIndex = playlist.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        scope.launch {
            // Check contextual event link (Liaison Musicale!)
            val linkedEvt = repository.getLinkedEventForTrack(track.id)
            repository.incrementPlays(track.id)

            _playbackState.value = _playbackState.value.copy(
                currentTrack = track,
                linkedEvent = linkedEvt,
                isPlaying = true,
                currentPositionSec = 0,
                durationSec = track.durationSec
            )

            startProgressTicker()
        }
    }

    fun togglePlayPause() {
        val current = _playbackState.value
        if (current.currentTrack == null && playlist.isNotEmpty()) {
            playTrack(playlist.first())
            return
        }

        val newPlaying = !current.isPlaying
        _playbackState.value = current.copy(isPlaying = newPlaying)

        if (newPlaying) {
            startProgressTicker()
        } else {
            playbackJob?.cancel()
        }
    }

    fun seekTo(seconds: Int) {
        val current = _playbackState.value
        val clamped = seconds.coerceIn(0, current.durationSec.coerceAtLeast(1))
        _playbackState.value = current.copy(currentPositionSec = clamped)
    }

    fun next() {
        if (playlist.isEmpty()) return
        if (_playbackState.value.isShuffle) {
            val randomIdx = playlist.indices.random()
            playTrack(playlist[randomIdx])
        } else {
            currentIndex = (currentIndex + 1) % playlist.size
            playTrack(playlist[currentIndex])
        }
    }

    fun previous() {
        if (playlist.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
        playTrack(playlist[currentIndex])
    }

    fun toggleShuffle() {
        val current = _playbackState.value
        _playbackState.value = current.copy(isShuffle = !current.isShuffle)
    }

    fun toggleRepeat() {
        val current = _playbackState.value
        _playbackState.value = current.copy(isRepeat = !current.isRepeat)
    }

    fun setAudioQuality(quality: String) {
        _playbackState.value = _playbackState.value.copy(quality = quality)
    }

    fun toggleOfflineMode() {
        val current = _playbackState.value
        val newMode = !current.isOfflineModeActive
        _playbackState.value = current.copy(isOfflineModeActive = newMode)
    }

    private fun startProgressTicker() {
        playbackJob?.cancel()
        playbackJob = scope.launch {
            while (isActive && _playbackState.value.isPlaying) {
                delay(1000)
                val state = _playbackState.value
                val nextPos = state.currentPositionSec + 1
                if (nextPos >= state.durationSec) {
                    if (state.isRepeat) {
                        _playbackState.value = state.copy(currentPositionSec = 0)
                    } else {
                        next()
                    }
                } else {
                    _playbackState.value = state.copy(currentPositionSec = nextPos)
                }
            }
        }
    }
}
