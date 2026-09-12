package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrackEntity
import com.example.player.PlaybackState
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    allTracks: List<TrackEntity>,
    playbackState: PlaybackState,
    onPlayTrack: (TrackEntity) -> Unit,
    onDownloadTrack: (TrackEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Tous") }
    val genres = listOf("Tous", "Mbalax", "Rap Galsen", "Afrobeat", "Acoustic", "Amapiano")

    val searchResults = remember(searchQuery, selectedGenre, allTracks) {
        allTracks.filter { track ->
            val matchQuery = searchQuery.isBlank() ||
                    track.title.contains(searchQuery, ignoreCase = true) ||
                    track.artistName.contains(searchQuery, ignoreCase = true) ||
                    track.city.contains(searchQuery, ignoreCase = true) ||
                    track.lyrics.contains(searchQuery, ignoreCase = true)

            val matchGenre = selectedGenre == "Tous" || track.genre.equals(selectedGenre, ignoreCase = true)

            matchQuery && matchGenre
        }
    }

    Box(
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("search_screen")
        ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un son, artiste, ville...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Genre filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            genres.forEach { g ->
                FilterChip(
                    selected = selectedGenre == g,
                    onClick = { selectedGenre = g },
                    label = { Text(g, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${searchResults.size} résultat(s) trouvé(s)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { track ->
                TrackRowItem(
                    track = track,
                    isPlaying = playbackState.isPlaying,
                    isCurrentTrack = playbackState.currentTrack?.id == track.id,
                    hasLiveEvent = track.id in listOf("track_1", "track_2", "track_3"),
                    onPlayClick = { onPlayTrack(track) },
                    onDownloadClick = { onDownloadTrack(track) }
                )
            }
        }
    }
}
}
