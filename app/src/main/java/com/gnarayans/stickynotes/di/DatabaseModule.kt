package com.gnarayans.stickynotes.di

import android.content.Context
import androidx.room.Room
import com.gnarayans.stickynotes.data.local.NoteDao
import com.gnarayans.stickynotes.data.local.StickyNotesDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): StickyNotesDatabase =
        Room.databaseBuilder(context, StickyNotesDatabase::class.java, StickyNotesDatabase.NAME)
            .build()

    @Provides
    fun provideNoteDao(database: StickyNotesDatabase): NoteDao = database.noteDao()
}
