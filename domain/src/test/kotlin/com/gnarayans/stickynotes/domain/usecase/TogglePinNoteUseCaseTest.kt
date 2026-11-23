package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.fake.FakeNoteRepository
import com.gnarayans.stickynotes.domain.model.Note
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TogglePinNoteUseCaseTest {

    @Test
    fun `toggling pin flips the pinned flag`() = runTest {
        val note = Note(id = 1, title = "Pin me", content = "", createdAt = 0, updatedAt = 0)
        val repository = FakeNoteRepository(listOf(note))
        val togglePin = TogglePinNoteUseCase(repository)

        togglePin(note)
        assertTrue(repository.getNoteById(1)!!.isPinned)

        togglePin(repository.getNoteById(1)!!)
        assertFalse(repository.getNoteById(1)!!.isPinned)
    }
}
