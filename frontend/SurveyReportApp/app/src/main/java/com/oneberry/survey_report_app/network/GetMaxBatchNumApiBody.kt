package com.oneberry.survey_report_app.network

import com.oneberry.survey_report_app.data.SurveyReport

data class GetMaxBatchNumApiBody (
    val items: List<BatchNumItems>
)
data class BatchNumItems (
    val batchNum: Int
)