package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import com.oneberry.survey_report_app.network.SurveyRequestId
import com.oneberry.survey_report_app.ui.screens.preview_screen.PreviewNavRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PastSubmissionsViewModel(
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
                PastSubmissionsViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    backendAPI = backendAPI,
                    surveyReportRepository = surveyReportRepository,
                )
            }
        }
    }
    //Navigation
    private val _navRequest = MutableSharedFlow<PastSubmissionsNavRequest>()
    val navRequest = _navRequest.asSharedFlow()
    //Credentials data
    private val credentialFlow = userCredentialsRepository.credentialsFlow
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    // Screen state
    private val _pastSubmissions = MutableStateFlow<PastSubmissionsState?>(null)
    val pastSubmissions = _pastSubmissions.asStateFlow()

    init {
        //TODO: Figure out why this causes a crash when it gets a valid item
        //      (Probably just means I should call this from LaunchedEffect, try that first)
        updatePastSubmissions(1)
    }
    private fun updatePastSubmissions(page: Int) {
        viewModelScope.launch {
            val credentials = credentialFlow.first()
            if (credentials.username == null) {
                _toastMessage.emit("Error: Login credentials missing")
                _navRequest.emit(PastSubmissionsNavRequest.Login)
                return@launch
            }
            val nonNullCredentials =
                credentials.tryGetNotNullCredentials()
            if (nonNullCredentials == null) {
                _toastMessage.emit("Error: Login credentials missing")
                _navRequest.emit(
                    PastSubmissionsNavRequest.ReLogin(credentials.username)
                )
                return@launch
            }
            if (nonNullCredentials.isNotExpired(LocalDateTime.now(),false)) {
                _toastMessage.emit("You need to login again, your session has expired.")
                _navRequest.emit(
                    PastSubmissionsNavRequest.ReLogin(credentials.username)
                )
                return@launch
            }
            val pastSubmissionsData = backendAPI.getPastSubmissionsList(
                nonNullCredentials.token,
                nonNullCredentials.id,
                page,
            )
            if (pastSubmissionsData == null) {
                _toastMessage.emit("Something went wrong while loading.")
            } else {
                _pastSubmissions.update {
                    PastSubmissionsState(
                        page = pastSubmissionsData.page,
                        perPage = pastSubmissionsData.perPage,
                        totalItems = pastSubmissionsData.totalItems,
                        totalPages = pastSubmissionsData.totalPages,
                        items = pastSubmissionsData.items.map{
                            it.expand.surveyRequest
                        }
                    )
                }
            }
        }
    }
}