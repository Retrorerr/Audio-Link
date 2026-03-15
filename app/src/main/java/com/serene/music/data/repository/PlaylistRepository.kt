package com.serene.music.data.repository

import com.serene.music.data.local.db.PlaylistDao
import com.serene.music.data.model.Playlist
import com.serene.music.data.model.PlaylistSong
import com.serene.music.data.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicRepository: MusicRepository
) {
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs?> =
        combine(
            kotlinx.coroutines.flow.flow { emit(playlistDao.getPlaylistById(playlistId)) },
            playlistDao.getSongsForPlaylist(playlistId)
        ) { playlist, playlistSongs ->
            playlist?.let {
                val songs = playlistSongs
                    .sortedBy { it.position }
                    .mapNotNull { musicRepository.getSongById(it.songId) }
                PlaylistWithSongs(playlist = it, songs = songs)
            }
        }

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(Playlist(name = name))
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun renamePlaylist(playlist: Playlist, newName: String) {
        playlistDao.updatePlaylist(playlist.copy(name = newName))
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val count = playlistDao.getSongCount(playlistId)
        playlistDao.insertPlaylistSong(
            PlaylistSong(playlistId = playlistId, songId = songId, position = count)
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}
