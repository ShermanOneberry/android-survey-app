package com.oneberry.survey_report_app.ui.screens.preview_screen

import com.oneberry.survey_report_app.ui.screens.survey_report_screen.SurveyReportNavRequest

sealed class PreviewNavRequest {
    object Back : PreviewNavRequest()
    object Login : PreviewNavRequest()
    data class ReLogin(val username: String) : PreviewNavRequest()
}