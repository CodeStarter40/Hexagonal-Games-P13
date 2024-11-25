package com.openclassrooms.hexagonal.games.screen.login

//state possible pour l'interface user
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}