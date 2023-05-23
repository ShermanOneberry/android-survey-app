package com.oneberry.survey_report_app

import android.app.Application
import com.oneberry.survey_report_app.data.UserCredentialsRepository

class SurveyApplication : Application() {
    lateinit var userCredentialsRepository : UserCredentialsRepository
    override fun onCreate() {
        super.onCreate()
        userCredentialsRepository = UserCredentialsRepository(applicationContext)
    }
}