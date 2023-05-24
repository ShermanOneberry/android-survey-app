package com.oneberry.survey_report_app.ui.screens.preview_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository

class PreviewViewModel (
    private val userCredentialsRepository: UserCredentialsRepository,
    private val backendAPI: PocketBaseRepository,
): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SurveyApplication)
                val userCredentialsRepository =
                    app.userCredentialsRepository
                val backendAPI =
                    app.backendAPI
                PreviewViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    backendAPI = backendAPI,
                )
            }
        }
    }
    //TODO: Add code here
}