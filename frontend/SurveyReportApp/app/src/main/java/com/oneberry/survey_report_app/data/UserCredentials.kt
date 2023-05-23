package com.oneberry.survey_report_app.data

import java.time.LocalDateTime

data class UserCredentials(
    val username: String? = null,
    val id: String? = null,
    val token: String? = null,
    val goodUntilDateTime: LocalDateTime? = null,
) {
    fun isValidCredentials(currentTime: LocalDateTime): Boolean {
        return  (username?.length != 0) &&
                (id?.length != 0) &&
                (token?.length != 0) &&
                (goodUntilDateTime?.isAfter(currentTime) == true)
    }
}