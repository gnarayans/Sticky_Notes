package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.fake.FakeNoteRepository
import com.gnarayans.stickynotes.domain.model.Note
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveNoteUseCaseTest {

    private val repository = FakeNoteRepository()
    private val saveNote = SaveNoteUseCase(repository) { NOW }

    @Test
    fun `blank note is rejected`() = runTest {
        val result = saveNote(Note(title = "  ", content = "\n", createdAt = 0, updatedAt = 0))

        assertTrue(result.exceptionOrNull() is EmptyNoteException)
        assertTrue(repository.current.isEmpty())
    }

    @Test
    fun `new note is trimmed and stamped with creation and update time`() = runTest {
        val id = saveNote(Note(title = " Groceries ", content = " Milk ", createdAt = 0, updatedAt = 0))
            .getOrThrow()

        val saved = repository.getNoteById(id)!!
        assertEquals("Groceries", saved.title)
        assertEquals("Milk", saved.content)
        assertEquals(NOW, saved.createdAt)
        assertEquals(NOW, saved.updatedAt)
    }

    @Test
    fun `existing note keeps creation time and refreshes update time`() = runTest {
        val existing = Note(id = 7, title = "Todo", content = "", createdAt = 10, updatedAt = 20)
        repository.upsertNote(existing)

        saveNote(existing.copy(content = "Call mom")).getOrThrow()

        val saved = repository.getNoteById(7)!!
        assertEquals(10L, saved.createdAt)
        assertEquals(NOW, saved.updatedAt)
        assertEquals("Call mom", saved.content)
    }

    private companion object {
        const val NOW = 1_000L
    }
}
