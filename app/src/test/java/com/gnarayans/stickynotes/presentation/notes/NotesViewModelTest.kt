package com.gnarayans.stickynotes.presentation.notes

import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.usecase.DeleteNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.ObserveNotesUseCase
import com.gnarayans.stickynotes.domain.usecase.TogglePinNoteUseCase
import com.gnarayans.stickynotes.testing.FakeNoteRepository
import com.gnarayans.stickynotes.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val pinned = Note(id = 1, title = "Pinned", content = "", isPinned = true, createdAt = 0, updatedAt = 1)
    private val recent = Note(id = 2, title = "Recent", content = "", createdAt = 0, updatedAt = 5)
    private val repository = FakeNoteRepository(listOf(recent, pinned))

    private fun createViewModel() = NotesViewModel(
        observeNotes = ObserveNotesUseCase(repository),
        togglePinNote = TogglePinNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository),
    )

    @Test
    fun `state contains sorted notes once loaded`() {
        val state = createViewModel().state.value

        assertFalse(state.isLoading)
        assertEquals(listOf(pinned, recent), state.notes)
    }

    @Test
    fun `add note intent emits open effect for a new note`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(NotesIntent.AddNoteClicked)

        assertEquals(NotesEffect.OpenNote(Note.NEW_NOTE_ID), viewModel.effects.first())
    }

    @Test
    fun `note clicked intent emits open effect for that note`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(NotesIntent.NoteClicked(2))

        assertEquals(NotesEffect.OpenNote(2), viewModel.effects.first())
    }

    @Test
    fun `toggle pin intent updates state`() {
        val viewModel = createViewModel()

        viewModel.onIntent(NotesIntent.TogglePinClicked(recent))

        assertTrue(viewModel.state.value.notes.first { it.id == 2L }.isPinned)
    }

    @Test
    fun `delete intent removes note and emits deleted effect`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(NotesIntent.DeleteClicked(recent))

        assertEquals(listOf(pinned), viewModel.state.value.notes)
        assertEquals(NotesEffect.NoteDeleted, viewModel.effects.first())
    }
}
