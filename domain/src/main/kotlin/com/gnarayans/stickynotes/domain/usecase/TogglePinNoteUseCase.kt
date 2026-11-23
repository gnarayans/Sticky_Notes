package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import javax.inject.Inject

class TogglePinNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) {
        repository.upsertNote(note.copy(isPinned = !note.isPinned))
    }
}
