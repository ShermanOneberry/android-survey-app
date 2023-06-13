package com.oneberry.survey_report_app.network.api_body

data class GetMaxBatchNumApiBody (
    val items: List<BatchNumItems>
)
data class BatchNumItems (
    val batchNumber: Int
)