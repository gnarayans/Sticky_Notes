package com.gnarayans.stickynotes.presentation.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnarayans.stickynotes.R
import com.gnarayans.stickynotes.domain.model.Note
import com.gnarayans.stickynotes.domain.model.NoteColor
import com.gnarayans.stickynotes.presentation.components.StickyNoteCard
import com.gnarayans.stickynotes.presentation.mvi.ObserveAsEffects
import com.gnarayans.stickynotes.presentation.theme.StickyNotesTheme

/** Stateful entry point: wires the [NotesViewModel] to the stateless [NotesContent]. */
@Composable
fun NotesScreen(
    onOpenNote: (Long) -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            is NotesEffect.OpenNote -> onOpenNote(effect.noteId)
            NotesEffect.NoteDeleted ->
                snackbarHostState.showSnackbar(resources.getString(R.string.note_deleted))
        }
    }

    NotesContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesContent(
    state: NotesState,
    onIntent: (NotesIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notes_title)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(NotesIntent.AddNoteClicked) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.add_note),
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.notes.isEmpty() -> Text(
                    text = stringResource(R.string.notes_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
                else -> LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(items = state.notes, key = { it.id }) { note ->
                        StickyNoteCard(
                            note = note,
                            onClick = { onIntent(NotesIntent.NoteClicked(note.id)) },
                            onTogglePin = { onIntent(NotesIntent.TogglePinClicked(note)) },
                            onDelete = { onIntent(NotesIntent.DeleteClicked(note)) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesContentPreview() {
    StickyNotesTheme(dynamicColor = false) {
        NotesContent(
            state = NotesState(
                isLoading = false,
                notes = listOf(
                    Note(1, "Groceries", "Milk, eggs, bread", NoteColor.Yellow, true, 0, 0),
                    Note(2, "Ideas", "Build a sticky notes app with MVI", NoteColor.Blue, false, 0, 0),
                    Note(3, "", "Call the dentist", NoteColor.Pink, false, 0, 0),
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesContentEmptyPreview() {
    StickyNotesTheme(dynamicColor = false) {
        NotesContent(state = NotesState(isLoading = false), onIntent = {})
    }
}
