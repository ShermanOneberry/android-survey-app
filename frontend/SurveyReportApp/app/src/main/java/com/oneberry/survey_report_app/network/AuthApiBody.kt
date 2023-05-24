package com.oneberry.survey_report_app.network

import java.time.LocalDateTime

data class AuthApiBody (
    val token: String,
    val record: UserRecord,
)
data class UserRecord (
    val id: String,
)
data class AuthApiData (
    val id: String,
    val token: String,
    val tokenObtainedTime: LocalDateTime,
)