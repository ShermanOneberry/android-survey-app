package com.oneberry.survey_report_app.ui.screens.survey_report_screen

sealed class SurveyReportNavRequest {
    object Login : SurveyReportNavRequest()
    object Preview : SurveyReportNavRequest()
    data class ReLogin(val username: String) : SurveyReportNavRequest()
}