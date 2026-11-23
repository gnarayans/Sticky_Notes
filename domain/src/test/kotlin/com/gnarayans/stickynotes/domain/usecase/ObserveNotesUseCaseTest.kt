package com.gnarayans.stickynotes.domain.usecase

import com.gnarayans.stickynotes.domain.fake.FakeNoteRepository
import com.gnarayans.stickynotes.domain.model.Note
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveNotesUseCaseTest {

    @Test
    fun `notes are sorted pinned first then by most recently updated`() = runTest {
        val repository = FakeNoteRepository(
            listOf(
                note(id = 1, updatedAt = 100),
                note(id = 2, updatedAt = 300),
                note(id = 3, updatedAt = 50, isPinned = true),
                note(id = 4, updatedAt = 200, isPinned = true),
            ),
        )

        val result = ObserveNotesUseCase(repository)().first()

        assertEquals(listOf(4L, 3L, 2L, 1L), result.map { it.id })
    }

    private fun note(id: Long, updatedAt: Long, isPinned: Boolean = false) = Note(
        id = id,
        title = "Note $id",
        content = "",
        isPinned = isPinned,
        createdAt = 0,
        updatedAt = updatedAt,
    )
}
