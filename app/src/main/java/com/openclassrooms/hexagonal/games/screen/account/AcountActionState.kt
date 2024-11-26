package com.openclassrooms.hexagonal.games.screen.account

sealed class AccountActionState {
    object Idle : AccountActionState()
    object LogoutSuccess : AccountActionState()
    object DeleteSuccess : AccountActionState()
    object Loading : AccountActionState()
    data class Error(val message: String) : AccountActionState()
}