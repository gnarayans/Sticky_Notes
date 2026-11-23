package com.gnarayans.stickynotes.presentation.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnarayans.stickynotes.R
import com.gnarayans.stickynotes.domain.model.NoteColor
import com.gnarayans.stickynotes.presentation.editor.NoteEditorEffect.ShowError.Reason
import com.gnarayans.stickynotes.presentation.mvi.ObserveAsEffects
import com.gnarayans.stickynotes.presentation.theme.OnNoteColor
import com.gnarayans.stickynotes.presentation.theme.StickyNotesTheme
import com.gnarayans.stickynotes.presentation.theme.toColor

/** Stateful entry point: wires the [NoteEditorViewModel] to the stateless [NoteEditorContent]. */
@Composable
fun NoteEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            NoteEditorEffect.NavigateBack -> onNavigateBack()
            is NoteEditorEffect.ShowError -> {
                val message = when (effect.reason) {
                    Reason.EmptyNote -> R.string.error_empty_note
                    Reason.Unknown -> R.string.error_generic
                }
                snackbarHostState.showSnackbar(resources.getString(message))
            }
        }
    }

    BackHandler { viewModel.onIntent(NoteEditorIntent.BackClicked) }

    NoteEditorContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorContent(
    state: NoteEditorState,
    onIntent: (NoteEditorIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val paperColor by animateColorAsState(targetValue = state.color.toColor(), label = "paper")

    Scaffold(
        modifier = modifier,
        containerColor = paperColor,
        contentColor = OnNoteColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (state.isNewNote) R.string.new_note else R.string.edit_note))
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(NoteEditorIntent.BackClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
                actions = {
                    if (!state.isNewNote) {
                        IconButton(onClick = { onIntent(NoteEditorIntent.DeleteClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.delete_note),
                            )
                        }
                    }
                    IconButton(
                        onClick = { onIntent(NoteEditorIntent.SaveClicked) },
                        enabled = !state.isSaving && !state.isLoading,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = stringResource(R.string.save_note),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = OnNoteColor,
                    navigationIconContentColor = OnNoteColor,
                    actionIconContentColor = OnNoteColor,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            NoteEditorFields(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
        }
    }
}

@Composable
private fun NoteEditorFields(
    state: NoteEditorState,
    onIntent: (NoteEditorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        ColorPicker(
            selectedColor = state.color,
            onColorSelected = { onIntent(NoteEditorIntent.ColorSelected(it)) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        TextField(
            value = state.title,
            onValueChange = { onIntent(NoteEditorIntent.TitleChanged(it)) },
            placeholder = { Text(stringResource(R.string.note_title_hint)) },
            textStyle = MaterialTheme.typography.headlineSmall,
            colors = transparentTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = state.content,
            onValueChange = { onIntent(NoteEditorIntent.ContentChanged(it)) },
            placeholder = { Text(stringResource(R.string.note_content_hint)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = transparentTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColorPicker(
    selectedColor: NoteColor,
    onColorSelected: (NoteColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.selectableGroup(),
    ) {
        NoteColor.entries.forEach { color ->
            val description = stringResource(R.string.note_color, color.name)
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.toColor(), CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = OnNoteColor.copy(alpha = if (isSelected) 0.8f else 0.2f),
                        shape = CircleShape,
                    )
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onColorSelected(color) },
                    )
                    .semantics { contentDescription = description },
            )
        }
    }
}

@Composable
private fun transparentTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = OnNoteColor,
    unfocusedTextColor = OnNoteColor,
    cursorColor = OnNoteColor,
    focusedPlaceholderColor = OnNoteColor.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = OnNoteColor.copy(alpha = 0.5f),
)

@Preview(showBackground = true)
@Composable
private fun NoteEditorContentPreview() {
    StickyNotesTheme(dynamicColor = false) {
        NoteEditorContent(
            state = NoteEditorState(
                noteId = 1,
                title = "Groceries",
                content = "Milk\nEggs\nBread",
                color = NoteColor.Orange,
            ),
            onIntent = {},
        )
    }
}
