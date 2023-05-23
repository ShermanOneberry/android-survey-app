package com.oneberry.survey_report_app.ui.screens.login_screen

data class LoginUIState (
    val username: String = "",
    val password: String = "",
    val uiEnabled: Boolean = true,
    val successfulLogin: Boolean = false,
)