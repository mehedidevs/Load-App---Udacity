package com.mehedi.loadapp.ui.custom.state

sealed class ButtonState {
    data object Clicked : ButtonState()
    data object Loading : ButtonState()
    data object Completed : ButtonState()
}