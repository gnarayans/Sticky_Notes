package com.gnarayans.stickynotes.presentation.notes

import androidx.lifecycle.viewModelScope
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.usecase.DeleteNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.ObserveNotesUseCase
import com.gnarayans.stickynotes.domain.usecase.TogglePinNoteUseCase
import com.gnarayans.stickynotes.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    observeNotes: ObserveNotesUseCase,
    private val togglePinNote: TogglePinNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : MviViewModel<NotesState, NotesIntent, NotesEffect>(NotesState()) {

    init {
        observeNotes()
            .onEach { notes -> setState { copy(isLoading = false, notes = notes) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: NotesIntent) {
        when (intent) {
            NotesIntent.AddNoteClicked -> sendEffect(NotesEffect.OpenNote(Note.NEW_NOTE_ID))
            is NotesIntent.NoteClicked -> sendEffect(NotesEffect.OpenNote(intent.noteId))
            is NotesIntent.TogglePinClicked -> viewModelScope.launch { togglePinNote(intent.note) }
            is NotesIntent.DeleteClicked -> viewModelScope.launch {
                deleteNote(intent.note.id)
                sendEffect(NotesEffect.NoteDeleted)
            }
        }
    }
}
