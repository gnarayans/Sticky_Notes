package com.gnarayans.stickynotes.presentation.mvi

/** Immutable snapshot of everything a screen needs to render. */
interface UiState

/** A user action (or system event) that the screen sends to its ViewModel. */
interface UiIntent

/** A one-off side effect (navigation, snackbar, ...) that must be consumed exactly once. */
interface UiEffect
