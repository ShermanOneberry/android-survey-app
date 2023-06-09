package com.oneberry.survey_report_app.network

data class GetSubmissionsApiBody (
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val items: List<ItemsData>
)
data class ItemsData (
    val expand: Expand
)
data class Expand (
    val surveyRequest: SurveyRequestId,
)
data class SurveyRequestId (
    val batchNumber: Int,
    val batchID: Int,
)