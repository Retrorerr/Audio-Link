package com.serene.music.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule
// Remaining bindings provided via @Inject constructors (MediaStoreScanner, Repositories)
