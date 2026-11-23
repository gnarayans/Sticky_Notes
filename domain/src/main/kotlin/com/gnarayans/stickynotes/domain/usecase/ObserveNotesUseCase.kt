package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Streams all notes, pinned notes first and then most recently updated first.
 */
class ObserveNotesUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    operator fun invoke(): Flow<List<Note>> =
        repository.observeNotes().map { notes ->
            notes.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt },
            )
        }
}
