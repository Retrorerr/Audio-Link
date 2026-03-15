package com.serene.music.ui.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object NowPlaying : Screen("now_playing")
    data class AlbumDetail(val albumId: Long = 0L) : Screen("album/{albumId}") {
        companion object {
            const val ROUTE = "album/{albumId}"
            fun createRoute(albumId: Long) = "album/$albumId"
        }
    }
    data class ArtistDetail(val artistId: Long = 0L) : Screen("artist/{artistId}") {
        companion object {
            const val ROUTE = "artist/{artistId}"
            fun createRoute(artistId: Long) = "artist/$artistId"
        }
    }
    data class PlaylistDetail(val playlistId: Long = 0L) : Screen("playlist/{playlistId}") {
        companion object {
            const val ROUTE = "playlist/{playlistId}"
            fun createRoute(playlistId: Long) = "playlist/$playlistId"
        }
    }
}
