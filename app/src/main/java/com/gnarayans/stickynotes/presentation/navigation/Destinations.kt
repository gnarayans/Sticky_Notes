package com.gnarayans.stickynotes.presentation.navigation

import com.gnarayans.stickynotes.domain.model.Note
import kotlinx.serialization.Serializable

/** Type-safe navigation destinations. */
@Serializable
data object NotesDestination

@Serializable
data class NoteEditorDestination(val noteId: Long = Note.NEW_NOTE_ID)
