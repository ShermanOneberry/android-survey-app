package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

sealed class PastSubmissionsNavRequest {
    object Back : PastSubmissionsNavRequest()
    object Login : PastSubmissionsNavRequest()
    data class ReLogin(val username: String) : PastSubmissionsNavRequest()
}