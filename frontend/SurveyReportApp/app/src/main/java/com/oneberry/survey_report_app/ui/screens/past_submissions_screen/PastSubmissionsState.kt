package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import com.oneberry.survey_report_app.network.SurveyRequestId

data class PastSubmissionsState (
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val items: List<SurveyRequestId>
)