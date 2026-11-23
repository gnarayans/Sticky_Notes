package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteNote(id)
}
