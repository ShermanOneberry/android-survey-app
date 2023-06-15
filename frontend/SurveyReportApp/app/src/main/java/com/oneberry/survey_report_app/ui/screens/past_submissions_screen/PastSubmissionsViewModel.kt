package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.EditOnlyData
import com.oneberry.survey_report_app.data.NotNullUserCredentials
import com.oneberry.survey_report_app.data.SurveyReport
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.api_body.ItemsData
import com.oneberry.survey_report_app.network.PocketBaseRepository
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
    private val _pastSubmissions = MutableStateFlow(PastSubmissionsState())
    val pastSubmissions = _pastSubmissions.asStateFlow()

    init {
        updatePastSubmissions(1)
    }

    fun updateBlockFilter(newBlock: String) {
        _pastSubmissions.update {
            it.copy(
                searchBox = it.searchBox.copy(
                    block = newBlock
                )
            )
        }
    }

    fun updateStreetFilter(newStreet: String) {
        _pastSubmissions.update {
            it.copy(
                searchBox = it.searchBox.copy(
                    street = newStreet
                )
            )
        }
    }
    fun triggerListWithNewFilter(){
        _pastSubmissions.update {
            it.copy(
                searchBox = it.searchBox.copy(
                    blockFilter = it.searchBox.block,
                    streetFilter = it.searchBox.street,
                )
            )
        }
        updatePastSubmissions(1)
    }

    private suspend fun attemptGetCredentials(): NotNullUserCredentials? {
        val credentials = credentialFlow.first()
        if (credentials.username == null) {
            _toastMessage.emit("Error: Login credentials missing")
            _navRequest.emit(PastSubmissionsNavRequest.Login)
            return null
        }
        val nonNullCredentials =
            credentials.tryGetNotNullCredentials()
        if (nonNullCredentials == null) {
            _toastMessage.emit("Error: Login credentials missing")
            _navRequest.emit(
                PastSubmissionsNavRequest.ReLogin(credentials.username)
            )
            return null
        }
        if (nonNullCredentials.isNotExpired(LocalDateTime.now(),false)) {
            _toastMessage.emit("You need to login again, your session has expired.")
            _navRequest.emit(
                PastSubmissionsNavRequest.ReLogin(credentials.username)
            )
            return null
        }
        return nonNullCredentials
    }
    fun attemptGetNextPage() {
        val viewState = pastSubmissions.value.apiState
        if (viewState != null && viewState.page < viewState.totalPages) {
            updatePastSubmissions(viewState.page+1)
        }
    }
    fun attemptGetPrevPage() {
        val viewState = pastSubmissions.value.apiState
        if (viewState != null && viewState.page > 1) {
            updatePastSubmissions(viewState.page-1)
        }
    }
    private fun updatePastSubmissions(page: Int) {
        viewModelScope.launch {
            val nonNullCredentials = attemptGetCredentials() ?: return@launch
            val searchState = pastSubmissions.value.searchBox
            val pastSubmissionsData = backendAPI.getPastSubmissionsList(
                nonNullCredentials.token,
                searchState.blockFilter,
                searchState.streetFilter,
                page,
            )
            if (pastSubmissionsData == null) {
                _toastMessage.emit("Something went wrong while loading.")
            } else {
                var currentMaxBatchNumber = pastSubmissions.value.apiState?.latestBatchNumber
                if (currentMaxBatchNumber == null) {
                    currentMaxBatchNumber =
                        backendAPI.getMaxBatchNumber(nonNullCredentials.token)
                    if (currentMaxBatchNumber == null) {
                        _toastMessage.emit("Something went wrong while loading.")
                        return@launch
                    }
                }
                _pastSubmissions.update { it ->
                    it.copy (
                        searchBox = it.searchBox.copy(
                            block = it.searchBox.blockFilter,
                            street = it.searchBox.streetFilter,
                        ),
                        apiState = PastSubmissionsApiState(
                            page = pastSubmissionsData.page,
                            perPage = pastSubmissionsData.perPage,
                            totalPages = pastSubmissionsData.totalPages,
                            totalItems = pastSubmissionsData.totalItems,
                            items = pastSubmissionsData.items.map {item ->
                                AugmentedItemData(
                                    item = item,
                                    sameUser = item.assignedUser == nonNullCredentials.id
                                )
                            },

                            latestBatchNumber = currentMaxBatchNumber
                        )
                    )
                }
            }
        }
    }
    private suspend fun preparePastSubmission(item: ItemsData): SurveyReport? {
        val nonNullCredentials = attemptGetCredentials() ?: return null
        val surveyRequest = item.expand.surveyRequest
        val reasonImage = backendAPI.getImage(
            nonNullCredentials.token,
            item.collectionId,
            item.id,
            item.reasonImage
        )
        if (reasonImage == null) {
            _toastMessage.emit("Something went wrong while attempting to load images.")
            return null
        }
        val extraImage =
            if (item.additionalImage.isBlank()) null
            else {
                val image = backendAPI.getImage(
                    nonNullCredentials.token,
                    item.collectionId,
                    item.id,
                    item.reasonImage
                )
                if (image == null) {
                    _toastMessage.emit(
                        "Something went wrong while attempting to load images."
                    )
                    return null
                }
                image //To extraImage
            }
        return item.formData.copy(
            batchNum = surveyRequest.batchNumber.toString(),
            intraBatchId = surveyRequest.batchID.toString(),
            editOnlyData = EditOnlyData(
                recordId = item.id,
                reasonImage = reasonImage,
                extraImage = extraImage
            )
        )
    }
    fun editSubmission(item: ItemsData) {
        viewModelScope.launch {
            val preparedPastSubmission = preparePastSubmission(item) ?: return@launch
            surveyReportRepository.mutableSurveyState.update {
                preparedPastSubmission
            }
            _navRequest.emit(PastSubmissionsNavRequest.Back)
        }
    }
    fun viewSubmission(item: ItemsData) {
        viewModelScope.launch {
            val preparedPastSubmission = preparePastSubmission(item) ?: return@launch
            surveyReportRepository.mutableReviewPastSubmissionState.update {
                preparedPastSubmission
            }
            _navRequest.emit(PastSubmissionsNavRequest.View)
        }
    }
}