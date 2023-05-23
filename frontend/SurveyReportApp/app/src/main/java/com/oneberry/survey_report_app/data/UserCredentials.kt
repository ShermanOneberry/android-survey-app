package com.oneberry.survey_report_app.data

import java.time.LocalDateTime

data class UserCredentials( //Everything is Nullable because GSON does not respect non-nullable
    val username: String? = null,
    val id: String? = null,
    val token: String? = null,
    val validUntil: LocalDateTime? = null,
) {
    fun tryGetValidatedCredentials(currentTime: LocalDateTime): ValidatedUserCredentials? {
        return if (
            (!username.isNullOrEmpty()) &&
            (!id.isNullOrEmpty()) &&
            (!token.isNullOrEmpty()) &&
            (validUntil?.isAfter(currentTime) == true)
        ) {
            ValidatedUserCredentials(
                username = username,
                id = id,
                token = token,
                validUntil = validUntil,
            )
        } else {
            null
        }
    }
    fun isValidCredentials(currentTime: LocalDateTime):Boolean {
        return tryGetValidatedCredentials(currentTime) != null
    }
}
data class ValidatedUserCredentials(
    val username: String,
    val id: String,
    val token: String,
    val validUntil: LocalDateTime,
)