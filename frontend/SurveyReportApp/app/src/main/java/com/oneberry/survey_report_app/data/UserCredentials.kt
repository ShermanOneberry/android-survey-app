package com.oneberry.survey_report_app.data

import java.time.LocalDateTime

private const val TOKEN_LIFETIME_SECONDS:Long = 1209600
private const val ONE_HOUR_IN_SECONDS:Long = 60 * 60
private const val TOKEN_EARLY_EXPIRE_SECONDS:Long = ONE_HOUR_IN_SECONDS

data class UserCredentials( //Everything is Nullable because GSON does not respect non-nullable
    val username: String? = null,
    val id: String? = null,
    val token: String? = null,
    val tokenObtainedTime: LocalDateTime? = null,
) {
    fun tryGetNotNullCredentials(): NotNullUserCredentials? {
        return if (
            (!username.isNullOrEmpty()) &&
            (!id.isNullOrEmpty()) &&
            (!token.isNullOrEmpty()) &&
            (tokenObtainedTime != null)
        ) {
            NotNullUserCredentials(
                username = username,
                id = id,
                token = token,
                tokenObtainedTime = tokenObtainedTime,
            )
        } else {
            null
        }
    }
}
data class NotNullUserCredentials(
    val username: String,
    val id: String,
    val token: String,
    val tokenObtainedTime: LocalDateTime,
) {
    fun isNotExpired(
        currentTime: LocalDateTime,
        preemptiveExpiry: Boolean,
    ):Boolean {
        val validUntil =
            if (preemptiveExpiry) {
                tokenObtainedTime
                    .plusSeconds(TOKEN_LIFETIME_SECONDS)
                    .minusSeconds(TOKEN_EARLY_EXPIRE_SECONDS)
            } else {
                tokenObtainedTime
                    .plusSeconds(TOKEN_LIFETIME_SECONDS)
            }
        return validUntil.isBefore(currentTime)
    }
}