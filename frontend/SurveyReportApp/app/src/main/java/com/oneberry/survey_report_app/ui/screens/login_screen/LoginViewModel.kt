package com.oneberry.survey_report_app.ui.screens.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.UserCredentials
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel (
    private val userCredentialsRepository: UserCredentialsRepository,
    private val backendAPI: PocketBaseRepository
): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SurveyApplication)
                val userCredentialsRepository =
                    app.userCredentialsRepository
                val backendAPI =
                    app.backendAPI
                LoginViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    backendAPI = backendAPI,
                )
            }
        }
    }
    // UI state
    private val _uiState = MutableStateFlow(LoginUIState()) //TODO: Initialize username from navigation parameter
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun updateUsername(newUsername: String) {
        if (newUsername.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(username = newUsername)
        }
    }
    fun updatePassword(newPassword: String) {
        if (newPassword.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(password = newPassword)
        }
    }

    private fun canAttemptLogin(): Boolean {
        val (username, password) = uiState.value
        return !(username.isEmpty() || password.isEmpty())
    }
    private fun lockUi() {
        _uiState.update { currentState ->
            currentState.copy(uiEnabled = false)
        }
    }
    private suspend fun unlockUiWithError(message:String){
        _toastMessage.emit(message)
        _uiState.update { currentState ->
            currentState.copy(uiEnabled = true)
        }
    }

    fun attemptLogin() {
        lockUi()
        viewModelScope.launch {
            if (!canAttemptLogin()) {
                unlockUiWithError("Cannot have blank username or password")
                return@launch
            }
            val (username, password) = uiState.value
            val tokenResult = backendAPI.getApiToken(username, password)
            if (tokenResult == null) {
                unlockUiWithError("Could not login")
                return@launch
            }
            val (id, token, tokenObtainedTime) = tokenResult
            userCredentialsRepository.saveToPreferencesStore(UserCredentials(
                username = username,
                id = id,
                token = token,
                tokenObtainedTime = tokenObtainedTime,
            ))
            _uiState.update { currentState ->
                currentState.copy(successfulLogin = true)
            }
        }
    }

}