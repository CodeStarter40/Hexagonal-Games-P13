package com.openclassrooms.hexagonal.games.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.repository.LibraryRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    //fun pour connection firebase
    fun login(email:String,password:String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = userRepository.login(email, password)
        if (result.isSuccess) {
            _uiState.value = LoginUiState.Success
        } else {
            _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    //fun pour inscription firebase
    fun signUp(email:String,password:String,lastName:String,firstName:String) {
        _uiState.value = LoginUiState.Loading

        //vérifier si les champs lastname et firstname ne  sont pas empty
        if (lastName.isEmpty() || firstName.isEmpty()) {
            _uiState.value = LoginUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            val result = userRepository.signUp(email, password, lastName, firstName)
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Erreur lors de la création du compte")
            }
        }
    }
}