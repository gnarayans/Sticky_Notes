package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(id: Long): Note? = repository.getNoteById(id)
}
