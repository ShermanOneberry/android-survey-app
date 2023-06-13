package com.oneberry.survey_report_app

import com.oneberry.survey_report_app.data.SurveyReport
import com.oneberry.survey_report_app.network.PocketBaseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.nio.file.Paths

const val testURL: String = "http://127.0.0.1:8090"

@RunWith(RobolectricTestRunner::class)
class ApiUnitTest {
    @Test
    fun wrapper_canGetBearerToken() = runTest {
        val wrapper = PocketBaseRepository(testURL)
        val result = wrapper.getApiToken("dummy", "password1234")
        assertNotNull(result)
    }
    @Test
    fun wrapper_canHandleBadCredentials() = runTest {
        val wrapper = PocketBaseRepository(testURL)
        val result = wrapper.getApiToken("dummy", "password")
        assertNull(result)
    }
    @Test
    fun wrapper_uploadImage() = runTest { //TODO: Figure out how to reset this
        val wrapper = PocketBaseRepository(testURL)

        val testImageJavaURI =
            Paths.get(
                "src", "test", "java",
                "com", "oneberry", "survey_report_app",
                "resources", "test_image.jpg").toUri()
        val testImage = File(testImageJavaURI)

        val survey = SurveyReport(
            reasonImage = testImage
            )

        val tokenApiResult = wrapper
            .getApiToken("dummy", "password1234")
        if (tokenApiResult == null) fail("BearerToken should not be null")
        else {
            println(tokenApiResult.id)
            val surveyResultID = wrapper.uploadForm(
                tokenApiResult.token,
                tokenApiResult.id,
                survey
            )
            assertNotNull(surveyResultID)
        }


    }
}