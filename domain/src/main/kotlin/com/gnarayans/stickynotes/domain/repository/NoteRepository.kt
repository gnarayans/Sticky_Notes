package com.gnarayans.stickynotes.domain.repository

import com.gnarayans.stickynotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Contract for note persistence. Implemented in the data layer.
 */
interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: Long): Note?

    /** Inserts or updates [note] and returns its id. */
    suspend fun upsertNote(note: Note): Long

    suspend fun deleteNote(id: Long)
}
