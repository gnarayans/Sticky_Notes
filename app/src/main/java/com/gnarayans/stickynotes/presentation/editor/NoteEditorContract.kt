package com.gnarayans.stickynotes.presentation.editor

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.model.NoteColor
import com.gnarayans.stickynotes.presentation.mvi.UiEffect
import com.gnarayans.stickynotes.presentation.mvi.UiIntent
import com.gnarayans.stickynotes.presentation.mvi.UiState

data class NoteEditorState(
    val noteId: Long = Note.NEW_NOTE_ID,
    val title: String = "",
    val content: String = "",
    val color: NoteColor = NoteColor.Yellow,
    val isPinned: Boolean = false,
    val createdAt: Long = 0L,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
) : UiState {
    val isNewNote: Boolean get() = noteId == Note.NEW_NOTE_ID
}

sealed interface NoteEditorIntent : UiIntent {
    data class TitleChanged(val title: String) : NoteEditorIntent
    data class ContentChanged(val content: String) : NoteEditorIntent
    data class ColorSelected(val color: NoteColor) : NoteEditorIntent
    data object SaveClicked : NoteEditorIntent
    data object DeleteClicked : NoteEditorIntent
    data object BackClicked : NoteEditorIntent
}

sealed interface NoteEditorEffect : UiEffect {
    data object NavigateBack : NoteEditorEffect
    data class ShowError(val reason: Reason) : NoteEditorEffect {
        enum class Reason { EmptyNote, Unknown }
    }
}
