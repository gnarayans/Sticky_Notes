package com.gnarayans.stickynotes.testing

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNoteRepository(initial: List<Note> = emptyList()) : NoteRepository {
    private val notes = MutableStateFlow(initial.associateBy { it.id })
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    val current: List<Note> get() = notes.value.values.toList()

    override fun observeNotes(): Flow<List<Note>> = notes.map { it.values.toList() }

    override suspend fun getNoteById(id: Long): Note? = notes.value[id]

    override suspend fun upsertNote(note: Note): Long {
        val id = if (note.id == Note.NEW_NOTE_ID) nextId++ else note.id
        notes.update { it + (id to note.copy(id = id)) }
        return id
    }

    override suspend fun deleteNote(id: Long) {
        notes.update { it - id }
    }
}
