package com.oneberry.survey_report_app.network

import com.oneberry.survey_report_app.data.SurveyReport

data class GetSubmissionsApiBody (
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val items: List<ItemsData>
)
data class ItemsData (
    val id: String,
    val assignedUser: String,

    val formData: SurveyReport,

    val reasonImage: String,
    val additionalImage: String,

    val expand: Expand
)
data class Expand (
    val surveyRequest: SurveyRequestId,
)
data class SurveyRequestId (
    val batchNumber: Int,
    val batchID: Int,
)