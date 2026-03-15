package com.serene.music.di

import android.content.Context
import androidx.room.Room
import com.serene.music.data.local.db.FavoriteDao
import com.serene.music.data.local.db.PlaylistDao
import com.serene.music.data.local.db.SereneDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSereneDatabase(@ApplicationContext context: Context): SereneDatabase {
        return Room.databaseBuilder(
            context,
            SereneDatabase::class.java,
            "serene_database"
        ).build()
    }

    @Provides
    fun providePlaylistDao(database: SereneDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideFavoriteDao(database: SereneDatabase): FavoriteDao = database.favoriteDao()
}
