package com.gnarayans.stickynotes.presentation.notes

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.presentation.mvi.UiEffect
import com.gnarayans.stickynotes.presentation.mvi.UiIntent
import com.gnarayans.stickynotes.presentation.mvi.UiState

data class NotesState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
) : UiState

sealed interface NotesIntent : UiIntent {
    data object AddNoteClicked : NotesIntent
    data class NoteClicked(val noteId: Long) : NotesIntent
    data class TogglePinClicked(val note: Note) : NotesIntent
    data class DeleteClicked(val note: Note) : NotesIntent
}

sealed interface NotesEffect : UiEffect {
    data class OpenNote(val noteId: Long) : NotesEffect
    data object NoteDeleted : NotesEffect
}
