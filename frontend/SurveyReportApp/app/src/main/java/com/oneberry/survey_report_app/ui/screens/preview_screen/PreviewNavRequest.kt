package com.oneberry.survey_report_app.ui.screens.preview_screen

sealed class PreviewNavRequest {
    object Back : PreviewNavRequest()
    data class ReLogin(val username: String) : PreviewNavRequest()
}