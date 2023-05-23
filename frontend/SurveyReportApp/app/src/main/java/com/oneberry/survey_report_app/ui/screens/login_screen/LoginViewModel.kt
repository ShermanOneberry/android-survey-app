package com.oneberry.survey_report_app.ui.screens.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun canAttemptLogin(): Boolean {
        val (username, password) = uiState.value
        return !(username.isEmpty() || password.isEmpty())
    }

    fun attemptLogin() {
        viewModelScope.launch {
            if (!canAttemptLogin()) {
                //TODO: Do something with a toast
                return@launch
            }
            val (username, password) = uiState.value
            //TODO
            //backendAPI.getApiToken(username, password)
            //Check if backend api gives ok, if so upload to datastore repo and return to form
        }
    }

}