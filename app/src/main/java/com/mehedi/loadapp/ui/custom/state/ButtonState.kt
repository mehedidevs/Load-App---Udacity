package com.mehedi.loadapp.ui.custom.state

sealed class ButtonState {
    data object NORMAL : ButtonState()
    data object LOADING : ButtonState()
    data object COMPLETED : ButtonState()
}

