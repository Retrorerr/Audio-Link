package com.serene.music.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serene.music.data.model.PlaylistWithSongs
import com.serene.music.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlistWithSongs = MutableStateFlow<PlaylistWithSongs?>(null)
    val playlistWithSongs: StateFlow<PlaylistWithSongs?> = _playlistWithSongs.asStateFlow()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.getPlaylistWithSongs(playlistId).collect {
                _playlistWithSongs.value = it
            }
        }
    }

    fun removeSong(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistWithSongs.value?.playlist?.let {
                playlistRepository.deletePlaylist(it)
            }
        }
    }
}
