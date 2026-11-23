package com.gnarayans.stickynotes.presentation.theme

import androidx.compose.ui.graphics.Color
import com.gnarayans.stickynotes.domain.model.NoteColor

/** Text colour used on top of the (always light) sticky-note paper colours. */
val OnNoteColor = Color(0xFF1F1B16)

fun NoteColor.toColor(): Color = when (this) {
    NoteColor.Yellow -> Color(0xFFFFF59D)
    NoteColor.Pink -> Color(0xFFF8BBD0)
    NoteColor.Blue -> Color(0xFFB3E5FC)
    NoteColor.Green -> Color(0xFFC8E6C9)
    NoteColor.Orange -> Color(0xFFFFCC80)
    NoteColor.Purple -> Color(0xFFD1C4E9)
}
