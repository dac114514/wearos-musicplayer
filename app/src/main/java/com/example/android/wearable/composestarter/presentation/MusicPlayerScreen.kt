/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearable.composestarter.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.Scaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.FilledIconButton
import androidx.compose.material3.Icon\nimport androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.material.icons.filled.PlayArrow\nimport androidx.compose.material.icons.filled.ArrowForward
import androidx.wear.compose.material3.ListItem
// import androidx.wear.compose.material3.ScrollableColumn
import com.example.android.wearable.composestarter.R
import com.example.android.wearable.composestarter.presentation.model.MusicTrack

/**
 * Main music player screen with sliding list layout
 */
@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    // Initialize the view model with context when screen is first composed
    LaunchedEffect(Unit) {
        // Context will be passed from the Activity
        // For now, we'll load sample tracks
        viewModel.loadMusicTracks()
    }

    Scaffold(
        if (uiState.playlist.isEmpty() && !uiState.isLoading) {
            // Show empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_music_found),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Show music library
            MusicLibraryList(
                playlist = uiState.playlist,
                currentTrack = uiState.currentTrack,
                isPlaying = uiState.isPlaying,
                isLoading = uiState.isLoading,
                onTrackSelected = { track ->
                    viewModel.playTrack(track)
                },
                onPlayPause = { viewModel.togglePlayPause() },
                onNextTrack = { viewModel.skipToNext() },
                onPreviousTrack = { viewModel.skipToPrevious() }
            )
        }
    }
}

/**
 * Music library list component
 */
@Composable
private fun MusicLibraryList(
    playlist: List<MusicTrack>,
    currentTrack: MusicTrack?,
    isPlaying: Boolean,
    isLoading: Boolean,
    onTrackSelected: (MusicTrack) -> Unit,
    onPlayPause: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Current playing track info
        currentTrack?.let { track ->
            TitleCard(
                title = { 
                    Text(
                        text = stringResource(R.string.current_song),
                        // style = androidx.compose.material3.Typography().title3
                    )
                },
                onClick = { onTrackSelected(track) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${track.title}${if (track.artist != null) " - ${track.artist}" else ""}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Control buttons
        ButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            FilledIconButton(
                onClick = onPreviousTrack
            ) {
                Icon(Icons.Default.ArrowBack)
            }
            FilledIconButton(
                onClick = onPlayPause
            ) {
                Icon(Icons.Default.PlayArrow)
            }
            FilledIconButton(
                onClick = onNextTrack
            ) {
                Icon(Icons.Default.ArrowForward)
            }
        }

        // Loading indicator
        if (isLoading) {
            Text(
                text = stringResource(R.string.loading_music),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Track list
        Column(
            playlist.forEach { track ->
                TrackListItem(
                    track = track,
                    isCurrentTrack = track.id == currentTrack?.id,
                    isPlaying = isPlaying && track.id == currentTrack?.id,
                    onClick = { onTrackSelected(track) }
                )
            }
        }
    }
}

/**
 * Individual track list item
 */
@Composable
private fun TrackListItem(
    track: MusicTrack,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        onClick = onClick,
        headline = track.title,
        supportingText = if (track.artist != null) "${track.artist} • ${track.formattedDuration}" else track.formattedDuration,
        modifier = Modifier.fillMaxWidth()
    )
}