package com.gnarayans.stickynotes.data.mapper

import com.gnarayans.stickynotes.data.local.NoteEntity
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.model.NoteColor

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    color = NoteColor.entries.firstOrNull { it.name == color } ?: NoteColor.Yellow,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    color = color.name,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
