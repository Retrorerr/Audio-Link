package com.serene.music.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.serene.music.service.MusicServiceConnection
import com.serene.music.ui.components.MiniPlayer
import com.serene.music.ui.screens.library.LibraryScreen
import com.serene.music.ui.screens.nowplaying.NowPlayingScreen
import com.serene.music.ui.screens.playlist.PlaylistScreen

@Composable
fun SereneNavGraph(musicServiceConnection: MusicServiceConnection) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showMiniPlayer = currentRoute != Screen.NowPlaying.route

    Scaffold(
        bottomBar = {
            if (showMiniPlayer) {
                MiniPlayer(
                    musicServiceConnection = musicServiceConnection,
                    onTap = { navController.navigate(Screen.NowPlaying.route) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(Screen.Library.route) {
                LibraryScreen(
                    musicServiceConnection = musicServiceConnection,
                    onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) },
                    onNavigateToAlbum = { albumId ->
                        navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                    },
                    onNavigateToArtist = { artistId ->
                        navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                    },
                    onNavigateToPlaylist = { playlistId ->
                        navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                    }
                )
            }

            composable(
                route = Screen.NowPlaying.route,
                enterTransition = { slideInVertically { it } },
                exitTransition = { slideOutVertically { it } }
            ) {
                NowPlayingScreen(
                    musicServiceConnection = musicServiceConnection,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AlbumDetail.ROUTE,
                arguments = listOf(navArgument("albumId") { type = NavType.LongType })
            ) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getLong("albumId") ?: 0L
                // AlbumDetailScreen would go here — using LibraryScreen for now
                LibraryScreen(
                    musicServiceConnection = musicServiceConnection,
                    onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) },
                    onNavigateToAlbum = {},
                    onNavigateToArtist = {},
                    onNavigateToPlaylist = {}
                )
            }

            composable(
                route = Screen.PlaylistDetail.ROUTE,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistScreen(
                    playlistId = playlistId,
                    musicServiceConnection = musicServiceConnection,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
                )
            }
        }
    }
}
