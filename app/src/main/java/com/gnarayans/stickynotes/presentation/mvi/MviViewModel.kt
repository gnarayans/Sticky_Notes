package com.gnarayans.stickynotes.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class implementing the MVI (Model-View-Intent) unidirectional data flow:
 *
 * View --[Intent]--> ViewModel --(reduce)--> [State] --> View
 *                                  \--> [Effect] (one-off) --> View
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    /** Single entry point for every user interaction. */
    abstract fun onIntent(intent: I)

    protected fun setState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
