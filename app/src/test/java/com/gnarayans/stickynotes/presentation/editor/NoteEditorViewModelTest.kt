package com.gnarayans.stickynotes.presentation.editor

import androidx.lifecycle.SavedStateHandle
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.model.NoteColor
import com.gnarayans.stickynotes.domain.usecase.DeleteNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.GetNoteUseCase
import com.gnarayans.stickynotes.domain.usecase.SaveNoteUseCase
import com.gnarayans.stickynotes.testing.FakeNoteRepository
import com.gnarayans.stickynotes.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val existing = Note(
        id = 3,
        title = "Todo",
        content = "Water plants",
        color = NoteColor.Green,
        createdAt = 10,
        updatedAt = 10,
    )
    private val repository = FakeNoteRepository(listOf(existing))

    private fun createViewModel(noteId: Long? = null) = NoteEditorViewModel(
        savedStateHandle = SavedStateHandle(
            noteId?.let { mapOf(NoteEditorViewModel.NOTE_ID_ARG to it) } ?: emptyMap(),
        ),
        getNote = GetNoteUseCase(repository),
        saveNote = SaveNoteUseCase(repository) { NOW },
        deleteNote = DeleteNoteUseCase(repository),
    )

    @Test
    fun `new note starts with empty state`() {
        val state = createViewModel().state.value

        assertTrue(state.isNewNote)
        assertEquals("", state.title)
        assertEquals("", state.content)
    }

    @Test
    fun `existing note is loaded into state`() {
        val state = createViewModel(noteId = 3).state.value

        assertFalse(state.isLoading)
        assertEquals("Todo", state.title)
        assertEquals("Water plants", state.content)
        assertEquals(NoteColor.Green, state.color)
    }

    @Test
    fun `editing intents reduce state`() {
        val viewModel = createViewModel()

        viewModel.onIntent(NoteEditorIntent.TitleChanged("Hello"))
        viewModel.onIntent(NoteEditorIntent.ContentChanged("World"))
        viewModel.onIntent(NoteEditorIntent.ColorSelected(NoteColor.Pink))

        val state = viewModel.state.value
        assertEquals("Hello", state.title)
        assertEquals("World", state.content)
        assertEquals(NoteColor.Pink, state.color)
    }

    @Test
    fun `saving a valid note persists it and navigates back`() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(NoteEditorIntent.TitleChanged("Groceries"))

        viewModel.onIntent(NoteEditorIntent.SaveClicked)

        assertEquals(NoteEditorEffect.NavigateBack, viewModel.effects.first())
        val saved = repository.current.single { it.title == "Groceries" }
        assertEquals(NOW, saved.createdAt)
    }

    @Test
    fun `saving a blank note shows an error`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(NoteEditorIntent.SaveClicked)

        assertEquals(
            NoteEditorEffect.ShowError(NoteEditorEffect.ShowError.Reason.EmptyNote),
            viewModel.effects.first(),
        )
        assertEquals(listOf(existing), repository.current)
    }

    @Test
    fun `deleting an existing note removes it and navigates back`() = runTest {
        val viewModel = createViewModel(noteId = 3)

        viewModel.onIntent(NoteEditorIntent.DeleteClicked)

        assertEquals(NoteEditorEffect.NavigateBack, viewModel.effects.first())
        assertTrue(repository.current.isEmpty())
    }

    private companion object {
        const val NOW = 1_000L
    }
}
