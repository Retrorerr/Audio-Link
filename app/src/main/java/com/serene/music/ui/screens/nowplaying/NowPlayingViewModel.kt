package com.serene.music.ui.screens.nowplaying

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serene.music.data.repository.MusicRepository
import com.serene.music.service.MusicServiceConnection
import com.serene.music.service.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NowPlayingUiState(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumArtUri: Uri? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = 0,
    val isFavorite: Boolean = false
)

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NowPlayingUiState())
    val uiState: StateFlow<NowPlayingUiState> = _uiState.asStateFlow()

    init {
        observePlayback()
        observeCurrentSong()
        pollPosition()
    }

    private fun observePlayback() {
        viewModelScope.launch {
            musicServiceConnection.playbackState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    isPlaying = state.isPlaying,
                    duration = state.duration,
                    shuffleEnabled = state.shuffleEnabled,
                    repeatMode = state.repeatMode
                )
            }
        }
    }

    private fun observeCurrentSong() {
        viewModelScope.launch {
            musicServiceConnection.currentSong.collect { mediaItem ->
                mediaItem?.let { item ->
                    val metadata = item.mediaMetadata
                    _uiState.value = _uiState.value.copy(
                        title = metadata.title?.toString() ?: "",
                        artist = metadata.artist?.toString() ?: "",
                        album = metadata.albumTitle?.toString() ?: "",
                        albumArtUri = metadata.artworkUri
                    )
                    // Check if favorite
                    val songId = item.mediaId.toLongOrNull()
                    if (songId != null) {
                        musicRepository.isFavorite(songId).collect { fav ->
                            _uiState.value = _uiState.value.copy(isFavorite = fav)
                        }
                    }
                }
            }
        }
    }

    private fun pollPosition() {
        viewModelScope.launch {
            while (isActive) {
                val position = musicServiceConnection.getCurrentPosition()
                _uiState.value = _uiState.value.copy(currentPosition = position)
                delay(500L)
            }
        }
    }

    fun playPause() = musicServiceConnection.playPause()
    fun seekToNext() = musicServiceConnection.seekToNext()
    fun seekToPrevious() = musicServiceConnection.seekToPrevious()
    fun seekTo(positionMs: Long) = musicServiceConnection.seekTo(positionMs)
    fun toggleShuffle() = musicServiceConnection.toggleShuffle()
    fun toggleRepeat() = musicServiceConnection.toggleRepeat()

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentSong = musicServiceConnection.currentSong.value
            val songId = currentSong?.mediaId?.toLongOrNull() ?: return@launch
            musicRepository.toggleFavorite(songId, _uiState.value.isFavorite)
        }
    }
}
