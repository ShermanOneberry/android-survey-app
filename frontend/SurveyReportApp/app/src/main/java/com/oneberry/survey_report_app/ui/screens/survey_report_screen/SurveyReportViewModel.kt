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
import com.oneberry.survey_report_app.data.GroundType
import com.oneberry.survey_report_app.data.LocationType
import com.oneberry.survey_report_app.data.SurveyReport
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentials
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

class SurveyReportViewModel (
    private val userCredentialsRepository: UserCredentialsRepository,
    private val surveyReportRepository: SurveyReportRepository,
): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as SurveyApplication)
                val userCredentialsRepository =
                    app.userCredentialsRepository
                val surveyReportRepository =
                    app.surveyReportRepository
                SurveyReportViewModel(
                    userCredentialsRepository = userCredentialsRepository,
                    surveyReportRepository = surveyReportRepository,
                )
            }
        }
    }
    // DataStore Credentials Repository
    val credentialLiveData = userCredentialsRepository.credentialsFlow.asLiveData()
    private val credentialFlow = userCredentialsRepository.credentialsFlow
    // UI state
    private val _surveyState = surveyReportRepository.mutableSurveyState
    val surveyState: StateFlow<SurveyReport> = _surveyState.asStateFlow()
    //Toast emitter
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    //Navigation
    private val _navRequest = MutableSharedFlow<SurveyReportNavRequest>()
    val navRequest = _navRequest.asSharedFlow()
    //Hooks
    fun logOut() {
        viewModelScope.launch {
            userCredentialsRepository.saveToPreferencesStore(
                UserCredentials()
            )
        }
    }

    fun updateBatchNum(newNum: String) {
        if (newNum.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(batchNum = newNum)
        }
    }
    fun updateIntraBatchId(newNum: String) {
        if (newNum.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(intraBatchId = newNum)
        }
    }
    fun updateIsFeasible(newOK: Boolean) {
        _surveyState.update { currentState ->
            currentState.copy(isFeasible = newOK)
        }
    }
    fun updateReasonImage(newPicture: File?) {
        _surveyState.update { currentState ->
            currentState.copy(reasonImage = newPicture)
        }
    }
    fun updateNonFeasibleExplanation(newExplain: String) {
        _surveyState.update { currentState ->
            currentState.copy(nonFeasibleExplanation = newExplain)
        }
    }
    fun updateLocationDistance(newLocationDistance: String) {
        if (!"^\\d*m\$".toRegex().matches(newLocationDistance)) return
        _surveyState.update { currentState ->
            currentState.copy(locationDistance = newLocationDistance)
        }
    }
    fun updateCameraCount(newCount: String) {
        if (newCount.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(cameraCount = newCount)
        }
    }
    fun updateBoxCount(newCount: String) {
        if (newCount.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(boxCount = newCount)
        }
    }

    fun updateLocationType(newLocationType: LocationType) {
        _surveyState.update { currentState ->
            currentState.copy(locationType = newLocationType)
        }
    }
    fun updateCorridorLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(corridorLevel = newLevel)
        }
    }
    fun updateStairwayLowerLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(stairwayLowerLevel = newLevel)
        }
    }
    fun updateGroundType(newGroundType: GroundType) {
        _surveyState.update { currentState ->
            currentState.copy(groundType = newGroundType)
        }
    }
    fun updateCarparkLevel(newLevel: String) {
        if (newLevel.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(carparkLevel = newLevel)
        }
    }
    fun updateBlockLocation(newBlk: String) {
        if (newBlk.contains("\n")) return
        if (!"^Blk [^ ]*\$".toRegex().matches(newBlk)) return
        _surveyState.update { currentState ->
            currentState.copy(blockLocation = newBlk)
        }
    }
    fun updateStreetLocation(newStreet: String) {
        if (newStreet.contains("\n")) return
        _surveyState.update { currentState ->
            currentState.copy(streetLocation = newStreet)
        }
    }
    fun updateNearbyDescription(newNearby: String) {
        _surveyState.update { currentState ->
            currentState.copy(nearbyDescription = newNearby)
        }
    }
    fun updateHasAdditionalNotes(newBoolean: Boolean) {
        _surveyState.update { currentState ->
            currentState.copy(hasAdditionalNotes = newBoolean)
        }
    }
    fun updateTechniciansNotes(newNotes: String) {
        _surveyState.update { currentState ->
            currentState.copy(techniciansNotes = newNotes)
        }
    }
    fun updateExtraImage(newPicture: File?) {
        _surveyState.update { currentState ->
            currentState.copy(extraImage = newPicture)
        }
    }
    fun triggerPreview() {
        viewModelScope.launch {
            val finalFormData =  surveyState.value.copy(submissionTime = LocalDateTime.now())
            if (!finalFormData.overallSurveyValid()) {
                emitToast("Current form is not valid")
                return@launch
            }
            val credentials = credentialFlow.first()
            if (credentials.username == null) {
                emitToast("You need to login to submit")
                return@launch
            }
            val nonNullCredentials =
                credentials.tryGetNotNullCredentials()
            if (nonNullCredentials == null) {
                emitToast("Please Login Again")
                return@launch
            }
            if (nonNullCredentials.isNotExpired(LocalDateTime.now(),true)) {
                emitToast("You need to login again, your session has expired.")
                _navRequest.emit(
                    SurveyReportNavRequest.ReLogin(credentials.username)
                )
                return@launch
            }
            _navRequest.emit(SurveyReportNavRequest.Preview)
        }
    }
    private suspend fun emitToast(message: String) {
        _toastMessage.emit(message) //TODO: Maybe have the toast duration be longer
        Log.d("toast_message", message)
    }
}