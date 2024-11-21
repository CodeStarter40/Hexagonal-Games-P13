package com.openclassrooms.hexagonal.games.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor( private val userRepository: UserRepository) : ViewModel() {

    private val _accountActionState = MutableStateFlow<AccountActionState>(AccountActionState.Idle)
    val accountActionState: StateFlow<AccountActionState> = _accountActionState

    //déconnexion
    fun logout() {
        userRepository.signOut()
        _accountActionState.value = AccountActionState.LogoutSuccess
    }

    //suppression du compte
    fun deleteAccount() {
        viewModelScope.launch {
            userRepository.deleteAccount { success, message ->
                if (success) {
                    _accountActionState.value = AccountActionState.DeleteSuccess
                } else {
                    _accountActionState.value = AccountActionState.Error(message ?: "Unknow error")
                }
            }
        }
    }
}
