package com.oneberry.survey_report_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SurveyReportRepository {
    val mutableSurveyState = MutableStateFlow(getFreshState())
    val mutableReviewPastSubmissionState: MutableStateFlow<SurveyReport?>
        = MutableStateFlow(null)
    private fun getFreshState(): SurveyReport {
        return SurveyReport() //Completely fresh form.
    }
    fun resetSurvey() {
        mutableSurveyState.update { getFreshState() }
        mutableReviewPastSubmissionState.update { null }
    }
}