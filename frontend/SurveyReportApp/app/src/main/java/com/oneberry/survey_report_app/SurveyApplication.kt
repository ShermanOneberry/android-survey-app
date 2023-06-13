package com.oneberry.survey_report_app

import android.app.Application
import com.oneberry.survey_report_app.data.AppBootRepository
import com.oneberry.survey_report_app.data.SurveyReportRepository
import com.oneberry.survey_report_app.data.UserCredentialsRepository
import com.oneberry.survey_report_app.network.PocketBaseRepository

const val API_URL = "http://10.0.2.2:8090"

class SurveyApplication : Application() {
    val surveyReportRepository = SurveyReportRepository()
    val backendAPI = PocketBaseRepository(API_URL)
    val appBootRepository = AppBootRepository()
    lateinit var userCredentialsRepository : UserCredentialsRepository
    override fun onCreate() {
        super.onCreate()
        userCredentialsRepository = UserCredentialsRepository(applicationContext)
    }
}