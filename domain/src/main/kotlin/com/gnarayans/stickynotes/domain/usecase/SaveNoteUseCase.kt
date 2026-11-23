package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import com.gnarayans.stickynotes.domain.util.Clock
import javax.inject.Inject

/**
 * Validates and persists a note. Blank notes are rejected.
 */
class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(note: Note): Result<Long> {
        if (note.isBlank) return Result.failure(EmptyNoteException())
        val now = clock.now()
        val toSave = note.copy(
            title = note.title.trim(),
            content = note.content.trim(),
            createdAt = if (note.id == Note.NEW_NOTE_ID) now else note.createdAt,
            updatedAt = now,
        )
        return runCatching { repository.upsertNote(toSave) }
    }
}

class EmptyNoteException : IllegalArgumentException("A note needs a title or some content")
