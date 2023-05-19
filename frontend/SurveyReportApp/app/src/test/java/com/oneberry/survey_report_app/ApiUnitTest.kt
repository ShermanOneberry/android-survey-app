package com.oneberry.survey_report_app

import com.oneberry.survey_report_app.network.PocketBaseRepository
import com.oneberry.survey_report_app.ui.screens.survey_report_screen.SurveyReportUIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.nio.file.Paths

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ApiUnitTest {
    @Test
    fun wrapper_canGetBearerToken() = runTest {
        val wrapper = PocketBaseRepository()
        val result = wrapper.getApiToken("dummy", "password1234")
        assertNotNull(result)
    }
    @Test
    fun wrapper_canHandleBadCredentials() = runTest {
        val wrapper = PocketBaseRepository()
        val result = wrapper.getApiToken("dummy", "password")
        assertNull(result)
    }
    @Test
    fun wrapper_uploadImage() = runTest { //TODO: Figure out how to reset this
        val wrapper = PocketBaseRepository()

        val testImageJavaURI =
            Paths.get(
                "src", "test", "java",
                "com", "oneberry", "survey_report_app",
                "resources", "test_image.jpg").toUri()
        val testImage = File(testImageJavaURI)

        val survey = SurveyReportUIState(
            reportingTeam = "dummy",
            reasonImage = testImage
            )
        val surveyRequestID = "b7z7sachnw3uqlg" //TODO: Replace this with auto generation
        val surveyResultID = wrapper.uploadForm(
            surveyRequestID, survey
        )
        assertNotNull(surveyResultID)
    }
}