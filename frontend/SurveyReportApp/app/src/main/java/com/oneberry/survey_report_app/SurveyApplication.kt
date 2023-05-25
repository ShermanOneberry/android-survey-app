package com.oneberry.survey_report_app

import android.app.Application
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository

class SurveyApplication : Application() {
    val surveyReportRepository = SurveyReportRepository()
    val backendAPI = PocketBaseRepository()
    lateinit var userCredentialsRepository : UserCredentialsRepository
    override fun onCreate() {
        super.onCreate()
        userCredentialsRepository = UserCredentialsRepository(applicationContext)
    }
}