package com.oneberry.survey_report_app.ui.screens.survey_report_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oneberry.survey_report_app.SurveyApplication
import com.oneberry.survey_report_app.data.UserCredentials
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

class SurveyReportViewModel (
    private val userCredentialsRepository: UserCredentialsRepository,
    private val backendAPI: PocketBaseRepository,
): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as SurveyApplication)
                val userCredentialsRepository =
                    app.userCredentialsRepository
                val backendAPI =
                    app.backendAPI
                SurveyReportViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    backendAPI = backendAPI,
                )
            }
        }
    }
    // DataStore Repository
    // TODO: Fix load jank (Or at least figure out whether to delibertly ignore the problem)
    //       Consider either having a special initialValue with loading State
    //       Alternatively, have StateFlow for ViewModel and LiveData for UI, as separate data structs
    //       Resources: https://bladecoder.medium.com/kotlins-flow-in-viewmodels-it-s-complicated-556b472e281a
    //       NOTE: Further reading suggests accessing LiveData in ViewModel using "viewModelScope.launch"
    val credentialsState: StateFlow<UserCredentials> =
        userCredentialsRepository.credentialsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserCredentials()
        )
    // UI state
    private val _uiState = MutableStateFlow(getFreshState())
    val uiState: StateFlow<SurveyReportUIState> = _uiState.asStateFlow()
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    //Init code
    private fun getFreshState(): SurveyReportUIState {
        return SurveyReportUIState( //Completely fresh form.
            reportingTeam = "Default Team" //TODO: Tie this to username, probably
        )
    }
    //Hooks
    fun updateBatchNum(newNum: String) {
        if (newNum.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(batchNum = newNum)
        }
    }
    fun updateIntraBatchId(newNum: String) {
        if (newNum.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(intraBatchId = newNum)
        }
    }
    fun updateIsFeasible(newOK: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isFeasible = newOK)
        }
    }
    fun updateReasonImage(newPicture: File?) {
        _uiState.update { currentState ->
            currentState.copy(reasonImage = newPicture)
        }
    }
    fun updateNonFeasibleExplanation(newExplain: String) {
        _uiState.update { currentState ->
            currentState.copy(nonFeasibleExplanation = newExplain)
        }
    }
    fun updateLocationDistance(newLocationDistance: String) {
        if (!"^\\d*m\$".toRegex().matches(newLocationDistance)) return
        _uiState.update { currentState ->
            currentState.copy(locationDistance = newLocationDistance)
        }
    }
    fun updateCameraCount(newCount: String) {
        if (newCount.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(cameraCount = newCount)
        }
    }
    fun updateBoxCount(newCount: String) {
        if (newCount.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(boxCount = newCount)
        }
    }

    fun updateLocationType(newLocationType: LocationType) {
        _uiState.update { currentState ->
            currentState.copy(locationType = newLocationType)
        }
    }
    fun updateCorridorLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(corridorLevel = newLevel)
        }
    }
    fun updateStairwayLowerLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(stairwayLowerLevel = newLevel)
        }
    }
    fun updateCarparkLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(carparkLevel = newLevel)
        }
    }
    fun updateBlockLocation(newBlk: String) {
        if (newBlk.contains("\n")) return
        if (!"^Blk [^ ]*\$".toRegex().matches(newBlk)) return
        _uiState.update { currentState ->
            currentState.copy(blockLocation = newBlk)
        }
    }
    fun updateStreetLocation(newStreet: String) {
        if (newStreet.contains("\n")) return
        _uiState.update { currentState ->
            currentState.copy(streetLocation = newStreet)
        }
    }
    fun updateNearbyDescription(newNearby: String) {
        _uiState.update { currentState ->
            currentState.copy(nearbyDescription = newNearby)
        }
    }
    fun updateTechniciansNotes(newNotes: String) {
        _uiState.update { currentState ->
            currentState.copy(techniciansNotes = newNotes)
        }
    }
    fun triggerSubmission() {
        //TODO: Have this trigger navigation to a preview screen/dialog instead of immediately submitting
        viewModelScope.launch {
            val finalFormData =  uiState.value.copy(submissionTime = LocalDateTime.now())
            if (!finalFormData.overallSurveyValid()) {
                emitToast("Current form is not valid")
                return@launch
            }
            val credentials = credentialsState.value
            if (credentials == null) {
                emitToast("You need to login to submit")
                return@launch
            }
            if (credentials.username == null) {
                emitToast("You need to login to submit")
                return@launch
            }
            val validatedCredentials =
                credentials.tryGetValidatedCredentials(LocalDateTime.now())
            if (validatedCredentials == null) {
                emitToast("You need to login again, your session has expired.")
                return@launch
            }
            val surveyRequestID = "b7z7sachnw3uqlg"
            val surveyResponseID = backendAPI.uploadForm(
                validatedCredentials.token,
                surveyRequestID,
                validatedCredentials.id,
                finalFormData
            )
            if (surveyResponseID == null) {
                _toastMessage.emit("Unable to submit form")
            } else {
                _toastMessage.emit("Submission successful (ID: $surveyRequestID)")
                _uiState.update { _ -> getFreshState()}
            }
        }
    }
    fun emitToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}