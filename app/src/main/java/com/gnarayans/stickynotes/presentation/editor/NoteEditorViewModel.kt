package com.gnarayans.stickynotes.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.usecase.DeleteNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.EmptyNoteException
import com.gnarayans.stickynotes.domain.usecase.GetNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.SaveNoteUseCase
import com.gnarayans.stickynotes.presentation.editor.NoteEditorEffect.ShowError.Reason
import com.gnarayans.stickynotes.presentation.mvi.MviViewModel
import com.gnarayans.stickynotes.presentation.navigation.NoteEditorDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNote: GetNoteUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : MviViewModel<NoteEditorState, NoteEditorIntent, NoteEditorEffect>(NoteEditorState()) {

    init {
        // Argument name matches the property of the type-safe [NoteEditorDestination] route.
        val noteId = savedStateHandle.get<Long>(NOTE_ID_ARG) ?: Note.NEW_NOTE_ID
        if (noteId != Note.NEW_NOTE_ID) loadNote(noteId)
    }

    override fun onIntent(intent: NoteEditorIntent) {
        when (intent) {
            is NoteEditorIntent.TitleChanged -> setState { copy(title = intent.title) }
            is NoteEditorIntent.ContentChanged -> setState { copy(content = intent.content) }
            is NoteEditorIntent.ColorSelected -> setState { copy(color = intent.color) }
            NoteEditorIntent.SaveClicked -> save()
            NoteEditorIntent.DeleteClicked -> delete()
            NoteEditorIntent.BackClicked -> sendEffect(NoteEditorEffect.NavigateBack)
        }
    }

    private fun loadNote(noteId: Long) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val note = getNote(noteId)
            if (note == null) {
                setState { copy(isLoading = false) }
                sendEffect(NoteEditorEffect.NavigateBack)
                return@launch
            }
            setState {
                copy(
                    noteId = note.id,
                    title = note.title,
                    content = note.content,
                    color = note.color,
                    isPinned = note.isPinned,
                    createdAt = note.createdAt,
                    isLoading = false,
                )
            }
        }
    }

    private fun save() {
        if (currentState.isSaving) return
        setState { copy(isSaving = true) }
        viewModelScope.launch {
            val state = currentState
            val note = Note(
                id = state.noteId,
                title = state.title,
                content = state.content,
                color = state.color,
                isPinned = state.isPinned,
                createdAt = state.createdAt,
                updatedAt = state.createdAt,
            )
            saveNote(note)
                .onSuccess { sendEffect(NoteEditorEffect.NavigateBack) }
                .onFailure { error ->
                    setState { copy(isSaving = false) }
                    val reason = if (error is EmptyNoteException) Reason.EmptyNote else Reason.Unknown
                    sendEffect(NoteEditorEffect.ShowError(reason))
                }
        }
    }

    private fun delete() {
        val noteId = currentState.noteId
        viewModelScope.launch {
            if (noteId != Note.NEW_NOTE_ID) deleteNote(noteId)
            sendEffect(NoteEditorEffect.NavigateBack)
        }
    }

    companion object {
        /** Must match [NoteEditorDestination.noteId]. */
        const val NOTE_ID_ARG = "noteId"
    }
}
