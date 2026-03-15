package com.serene.music.data.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val artistId: Long,
    val songCount: Int,
    val albumArtUri: Uri?,
    val year: Int = 0,
    val songs: List<Song> = emptyList()
)
