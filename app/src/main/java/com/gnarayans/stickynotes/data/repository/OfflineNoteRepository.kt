package com.gnarayans.stickynotes.data.repository

import com.gnarayans.stickynotes.data.local.NoteDao
import com.gnarayans.stickynotes.data.mapper.toDomain
import com.gnarayans.stickynotes.data.mapper.toEntity
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed (offline-first) implementation of [NoteRepository].
 */
class OfflineNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        noteDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? = noteDao.getById(id)?.toDomain()

    override suspend fun upsertNote(note: Note): Long {
        val rowId = noteDao.upsert(note.toEntity())
        return if (rowId == UPDATED_ROW_ID) note.id else rowId
    }

    override suspend fun deleteNote(id: Long) = noteDao.deleteById(id)

    private companion object {
        const val UPDATED_ROW_ID = -1L
    }
}
