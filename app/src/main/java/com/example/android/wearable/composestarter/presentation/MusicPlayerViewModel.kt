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

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.wearable.composestarter.presentation.model.MusicPlayerState
import com.example.android.wearable.composestarter.presentation.model.MusicTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
/**
 * ViewModel for music player functionality
 */
class MusicPlayerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MusicPlayerState())
    val uiState: StateFlow<MusicPlayerState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var context: Context? = null

    /**
     * Initialize the player with application context
     */
    fun initialize(context: Context) {
        this.context = context.applicationContext
        setupExoPlayer()
    }

    private fun setupExoPlayer() {
        exoPlayer = ExoPlayer.Builder(context!!).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updatePlayerState(playbackState)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    mediaItem?.let { item ->
                        // Find the corresponding track in the playlist
                        val track = _uiState.value.playlist.find { it.uri == item.requestMetadata.mediaUri.toString() }
                        if (track != null) {
                            _uiState.value = _uiState.value.copy(currentTrack = track)
                        }
                    }
                }
            })
        }
    }

    /**
     * Load music tracks from device storage
     */
    fun loadMusicTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // For demo purposes, we'll create some sample tracks
                // In a real app, you would query MediaStore or other sources
                val sampleTracks = createSampleTracks()
                
                _uiState.value = _uiState.value.copy(
                    playlist = sampleTracks,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load music: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Play a specific track
     */
    fun playTrack(track: MusicTrack) {
        val currentPlaylist = _uiState.value.playlist
        val updatedPlaylist = currentPlaylist.mapIndexed { index, t ->
            if (t.id == track.id) track else t
        }

        _uiState.value = _uiState.value.copy(playlist = updatedPlaylist)

        val mediaItems = updatedPlaylist.map { 
            MediaItem.fromUri(Uri.parse(it.uri))
        }

        exoPlayer?.apply {
            setMediaItems(mediaItems)
            seekTo(updatedPlaylist.indexOfFirst { it.id == track.id }, 0L)
            prepare()
            play()
        }
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        exoPlayer?.let { player ->
            when {
                player.isPlaying -> player.pause()
                player.mediaItemCount > 0 -> player.play()
                else -> loadMusicTracks()
            }
        }
    }

    /**
     * Skip to next track
     */
    fun skipToNext() {
        exoPlayer?.let { player ->
            if (player.hasNextMediaItem()) {
                player.seekToNext()
                player.play()
            }
        }
    }

    /**
     * Skip to previous track
     */
    fun skipToPrevious() {
        exoPlayer?.let { player ->
            if (player.hasPreviousMediaItem()) {
                player.seekToPrevious()
                player.play()
            }
        }
    }

    /**
     * Seek to position
     */
    fun seekToPosition(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    /**
     * Get current position
     */
    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    /**
     * Get duration
     */
    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }

    private fun updatePlayerState(playbackState: Int) {
        _uiState.value = _uiState.value.copy(
            playbackState = playbackState,
            positionMs = exoPlayer?.currentPosition ?: 0L,
            durationMs = exoPlayer?.duration ?: 0L
        )
    }

    /**
     * Create sample tracks for demonstration
     */
    private fun createSampleTracks(): List<MusicTrack> {
        return listOf(
            MusicTrack(
                id = 1L,
                title = "Sample Song 1",
                artist = "Demo Artist",
                album = "Demo Album",
                durationMs = TimeUnit.MINUTES.toMillis(3, 30), // 3:30
                uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            ),
            MusicTrack(
                id = 2L,
                title = "Sample Song 2", 
                artist = "Demo Artist",
                album = "Demo Album",
                durationMs = TimeUnit.MINUTES.toMillis(4, 15), // 4:15
                uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
            ),
            MusicTrack(
                id = 3L,
                title = "Sample Song 3",
                artist = "Demo Artist", 
                album = "Demo Album",
                durationMs = TimeUnit.MINUTES.toMillis(2, 45), // 2:45
                uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}