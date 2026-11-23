package com.gnarayans.stickynotes.di

import com.gnarayans.stickynotes.data.repository.OfflineNoteRepository
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import com.gnarayans.stickynotes.domain.util.Clock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: OfflineNoteRepository): NoteRepository

    companion object {
        @Provides
        @Singleton
        fun provideClock(): Clock = Clock { System.currentTimeMillis() }
    }
}
