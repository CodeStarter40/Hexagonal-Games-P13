package com.openclassrooms.hexagonal.games.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor( private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    //fun pour connection firebase
    fun login(email:String,password:String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = LoginUiState.Success
                    } else {
                        _uiState.value = LoginUiState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
        }
    }
    //fun pour inscription firebase
    fun signUp(email:String,password:String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = LoginUiState.Success
                    } else {
                        _uiState.value =
                            LoginUiState.Error(task.exception?.message ?: "Unknown error")
                    }
                }

        }
    }
}

//state possible pour l'interface user
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}