package com.serene.music.data.repository

import com.serene.music.data.local.MediaStoreScanner
import com.serene.music.data.local.db.FavoriteDao
import com.serene.music.data.model.Album
import com.serene.music.data.model.Artist
import com.serene.music.data.model.FavoriteSong
import com.serene.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val mediaStoreScanner: MediaStoreScanner,
    private val favoriteDao: FavoriteDao
) {
    private var cachedSongs: List<Song> = emptyList()

    fun getSongs(): Flow<List<Song>> = combine(
        flow { emit(mediaStoreScanner.scanSongs().also { cachedSongs = it }) },
        favoriteDao.getFavoriteSongIds()
    ) { songs, favoriteIds ->
        val favoriteSet = favoriteIds.toSet()
        songs.map { it.copy(isFavorite = it.id in favoriteSet) }
    }

    fun getAlbums(): Flow<List<Album>> = flow {
        val songs = if (cachedSongs.isEmpty()) {
            mediaStoreScanner.scanSongs().also { cachedSongs = it }
        } else {
            cachedSongs
        }
        val albums = songs
            .groupBy { it.albumId }
            .map { (albumId, albumSongs) ->
                val first = albumSongs.first()
                Album(
                    id = albumId,
                    name = first.album,
                    artist = first.artist,
                    artistId = first.artistId,
                    songCount = albumSongs.size,
                    albumArtUri = first.albumArtUri,
                    year = first.year,
                    songs = albumSongs.sortedBy { it.trackNumber }
                )
            }
            .sortedBy { it.name }
        emit(albums)
    }

    fun getArtists(): Flow<List<Artist>> = flow {
        val songs = if (cachedSongs.isEmpty()) {
            mediaStoreScanner.scanSongs().also { cachedSongs = it }
        } else {
            cachedSongs
        }
        val artists = songs
            .groupBy { it.artistId }
            .map { (artistId, artistSongs) ->
                val albums = artistSongs.groupBy { it.albumId }.map { (albumId, albumSongs) ->
                    val first = albumSongs.first()
                    Album(
                        id = albumId,
                        name = first.album,
                        artist = first.artist,
                        artistId = artistId,
                        songCount = albumSongs.size,
                        albumArtUri = first.albumArtUri,
                        year = first.year,
                        songs = albumSongs
                    )
                }
                Artist(
                    id = artistId,
                    name = artistSongs.first().artist,
                    albumCount = albums.size,
                    songCount = artistSongs.size,
                    albums = albums.sortedBy { it.name }
                )
            }
            .sortedBy { it.name }
        emit(artists)
    }

    fun getFavoriteSongs(): Flow<List<Song>> = combine(
        flow { emit(mediaStoreScanner.scanSongs()) },
        favoriteDao.getAllFavorites()
    ) { songs, favorites ->
        val favoriteIds = favorites.map { it.songId }.toSet()
        songs.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) }
    }

    fun isFavorite(songId: Long): Flow<Boolean> = favoriteDao.isFavorite(songId)

    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.removeFavorite(songId)
        } else {
            favoriteDao.addFavorite(FavoriteSong(songId = songId))
        }
    }

    fun getSongById(songId: Long): Song? = cachedSongs.find { it.id == songId }

    fun getAlbumById(albumId: Long): Album? {
        val albumSongs = cachedSongs.filter { it.albumId == albumId }
        if (albumSongs.isEmpty()) return null
        val first = albumSongs.first()
        return Album(
            id = albumId,
            name = first.album,
            artist = first.artist,
            artistId = first.artistId,
            songCount = albumSongs.size,
            albumArtUri = first.albumArtUri,
            year = first.year,
            songs = albumSongs.sortedBy { it.trackNumber }
        )
    }
}
