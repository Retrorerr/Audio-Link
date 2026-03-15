package com.serene.music.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.serene.music.data.model.FavoriteSong
import com.serene.music.data.model.Playlist
import com.serene.music.data.model.PlaylistSong

@Database(
    entities = [Playlist::class, PlaylistSong::class, FavoriteSong::class],
    version = 1,
    exportSchema = false
)
abstract class SereneDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
}
