package com.gnarayans.stickynotes.domain.model

/**
 * Core business entity representing a single sticky note.
 *
 * The domain layer is pure Kotlin and has no knowledge of Android, Room or Compose.
 */
data class Note(
    val id: Long = NEW_NOTE_ID,
    val title: String,
    val content: String,
    val color: NoteColor = NoteColor.Yellow,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
) {
    val isBlank: Boolean get() = title.isBlank() && content.isBlank()

    companion object {
        const val NEW_NOTE_ID = 0L
    }
}
