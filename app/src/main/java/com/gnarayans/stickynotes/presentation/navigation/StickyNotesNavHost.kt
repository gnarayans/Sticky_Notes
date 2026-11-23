package com.gnarayans.stickynotes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gnarayans.stickynotes.presentation.editor.NoteEditorScreen
import com.gnarayans.stickynotes.presentation.notes.NotesScreen

@Composable
fun StickyNotesNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NotesDestination,
        modifier = modifier,
    ) {
        composable<NotesDestination> {
            NotesScreen(
                onOpenNote = { noteId -> navController.navigate(NoteEditorDestination(noteId)) },
            )
        }
        composable<NoteEditorDestination> {
            NoteEditorScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
