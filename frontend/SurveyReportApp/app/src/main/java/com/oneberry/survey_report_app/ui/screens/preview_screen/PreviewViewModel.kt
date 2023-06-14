package com.oneberry.survey_report_app.ui.screens.preview_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.SurveyReport
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
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
    val viewOnlySurveyState = MutableStateFlow<SurveyReport?>(null)
    val viewOnlySurveyLoadedState = MutableStateFlow(false)
    //Credentials data
    private val credentialFlow = userCredentialsRepository.credentialsFlow
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    //Navigation
    private val _navRequest = MutableSharedFlow<PreviewNavRequest>()
    val navRequest = _navRequest.asSharedFlow()
    //Data loading
    fun attemptLoadViewOnlySurvey() {
        viewOnlySurveyState.update {
            surveyReportRepository.mutableReviewPastSubmissionState.value
        }
        surveyReportRepository.mutableReviewPastSubmissionState.update { null }
        viewOnlySurveyLoadedState.update { true }
    }
    //Hooks
    fun triggerSubmission() {
        viewModelScope.launch {
            val finalFormData = surveyState.value
            if (!finalFormData.overallSurveyValid()) {
                _toastMessage.emit("Error: Current form is not valid")
                _navRequest.emit(PreviewNavRequest.Back)
                return@launch
            }
            val credentials = credentialFlow.first()
            if (credentials.username == null) {
                _toastMessage.emit("Error: Login credentials missing")
                _navRequest.emit(PreviewNavRequest.Login)
                return@launch
            }
            val nonNullCredentials =
                credentials.tryGetNotNullCredentials()
            if (nonNullCredentials == null) {
                _toastMessage.emit("Error: Login credentials missing")
                _navRequest.emit(
                    PreviewNavRequest.ReLogin(credentials.username)
                )
                return@launch
            }
            if (nonNullCredentials.isNotExpired(LocalDateTime.now(),false)) {
                _toastMessage.emit("You need to login again, your session has expired.")
                _navRequest.emit(
                    PreviewNavRequest.ReLogin(credentials.username)
                )
                return@launch
            }
            val surveyResponseID = backendAPI.uploadForm(
                nonNullCredentials.token,
                nonNullCredentials.id,
                finalFormData
            )
            if (surveyResponseID == null) {
                _toastMessage.emit("Unable to submit form")
            } else {
                _toastMessage.emit("Submission successful")
                surveyReportRepository.resetSurvey()
                _navRequest.emit(PreviewNavRequest.Back)
            }
        }
    }
}