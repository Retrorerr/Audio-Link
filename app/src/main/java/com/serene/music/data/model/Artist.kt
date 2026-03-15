package com.serene.music.data.model

data class Artist(
    val id: Long,
    val name: String,
    val albumCount: Int,
    val songCount: Int,
    val albums: List<Album> = emptyList()
)
