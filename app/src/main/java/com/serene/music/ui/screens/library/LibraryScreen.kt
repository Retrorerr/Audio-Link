package com.serene.music.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serene.music.service.MusicServiceConnection
import com.serene.music.ui.components.PermissionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    musicServiceConnection: MusicServiceConnection,
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val tabs = listOf("Songs", "Albums", "Artists", "Playlists")
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSearch by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    PermissionScreen(onPermissionGranted = { viewModel.loadLibrary() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    if (showSearch) {
                        TextField(
                            value = searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = { Text("Search music...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Serene",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearch = !showSearch
                        if (!showSearch) viewModel.setSearchQuery("")
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> SongsScreen(
                    viewModel = viewModel,
                    musicServiceConnection = musicServiceConnection,
                    onNavigateToNowPlaying = onNavigateToNowPlaying
                )
                1 -> AlbumsScreen(
                    viewModel = viewModel,
                    onNavigateToAlbum = onNavigateToAlbum,
                    musicServiceConnection = musicServiceConnection,
                    onNavigateToNowPlaying = onNavigateToNowPlaying
                )
                2 -> ArtistsScreen(
                    viewModel = viewModel,
                    onNavigateToArtist = onNavigateToArtist
                )
                3 -> PlaylistsTab(
                    viewModel = viewModel,
                    onNavigateToPlaylist = onNavigateToPlaylist
                )
            }
        }
    }
}
