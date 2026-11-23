package com.gnarayans.stickynotes.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnarayans.stickynotes.R
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.model.NoteColor
import com.gnarayans.stickynotes.presentation.theme.OnNoteColor
import com.gnarayans.stickynotes.presentation.theme.StickyNotesTheme
import com.gnarayans.stickynotes.presentation.theme.toColor

@Composable
fun StickyNoteCard(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = note.color.toColor(),
            contentColor = OnNoteColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onTogglePin) {
                    Icon(
                        painter = painterResource(R.drawable.ic_push_pin),
                        contentDescription = stringResource(
                            if (note.isPinned) R.string.unpin_note else R.string.pin_note,
                        ),
                        modifier = Modifier
                            .size(20.dp)
                            .alpha(if (note.isPinned) 1f else 0.35f),
                    )
                }
            }
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 12.dp),
                )
            }
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.delete_note),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StickyNoteCardPreview() {
    StickyNotesTheme(dynamicColor = false) {
        StickyNoteCard(
            note = Note(1, "Groceries", "Milk, eggs, bread", NoteColor.Green, true, 0, 0),
            onClick = {},
            onTogglePin = {},
            onDelete = {},
        )
    }
}
