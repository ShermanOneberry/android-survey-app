package com.oneberry.survey_report_app.ui.screens.preview_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PreviewViewModel (
    private val userCredentialsRepository: UserCredentialsRepository,
    private val backendAPI: PocketBaseRepository,
    private val surveyReportRepository: SurveyReportRepository
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
                val surveyReportRepository =
                    app.surveyReportRepository
                PreviewViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    backendAPI = backendAPI,
                    surveyReportRepository = surveyReportRepository,
                )
            }
        }
    }
    //Survey data
    val surveyState = surveyReportRepository.mutableSurveyState.asStateFlow()
    //Credentials data
    private val credentialFlow = userCredentialsRepository.credentialsFlow
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    //Navigation
    private val _navRequest = MutableSharedFlow<PreviewNavRequest>()
    val navRequest = _navRequest.asSharedFlow()
    //Hooks
    //TODO: Add code here
    fun triggerSubmission() {
        //TODO: Adjust the toast messages to be more concerned with errors
        // suddenly popping up when they shouldn't
        viewModelScope.launch {
            val finalFormData =
                surveyState.value.copy(submissionTime = LocalDateTime.now())
            if (!finalFormData.overallSurveyValid()) {
                _toastMessage.emit("Current form is not valid")
                return@launch
            }
            val credentials = credentialFlow.first() //TODO: Check this with multiple user changes
            if (credentials.username == null) {
                _toastMessage.emit("You need to login to submit")
                return@launch
            }
            val nonNullCredentials =
                credentials.tryGetNotNullCredentials()
            if (nonNullCredentials == null) {
                _toastMessage.emit("Please Login Again")
                return@launch
            }
            if (nonNullCredentials.isNotExpired(LocalDateTime.now(),false)) {
                _toastMessage.emit("You need to login again, your session has expired.")
                //TODO: Trigger relogin dialog
                return@launch
            }
            val surveyRequestID = "b7z7sachnw3uqlg"
            val surveyResponseID = backendAPI.uploadForm(
                nonNullCredentials.token,
                surveyRequestID,
                nonNullCredentials.id,
                finalFormData
            )
            if (surveyResponseID == null) {
                _toastMessage.emit("Unable to submit form")
            } else {
                _toastMessage.emit("Submission successful (ID: $surveyRequestID)")
                surveyReportRepository.resetSurvey()
                _navRequest.emit(PreviewNavRequest.Back)
            }
        }
    }
}