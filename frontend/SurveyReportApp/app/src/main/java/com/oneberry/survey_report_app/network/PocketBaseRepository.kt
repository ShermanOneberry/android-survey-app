package com.oneberry.survey_report_app.network

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.oneberry.survey_report_app.data.SurveyReport
import io.goodforgod.gson.configuration.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.LocalDateTime

class PocketBaseRepository(apiUrl: String) {
    private val GSON = GsonFactory().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service: PocketBaseAPI = retrofit.create(PocketBaseAPI::class.java)

    suspend fun getApiToken(identity:String, password:String) : AuthApiData? {
        return withContext(Dispatchers.IO) {
            when (val response = service.getBearerToken(identity, password)) {
                is NetworkResponse.Success -> {
                    return@withContext AuthApiData(
                        id = response.body.record.id,
                        token = response.body.token,
                        tokenObtainedTime =
                            LocalDateTime.now(),
                    )
                }
                is NetworkResponse.Error -> return@withContext null
            }
        }
    }

    suspend fun uploadForm(
        bearerToken: String,
        userId: String,
        surveyData: SurveyReport,
    ) : String? {
        return withContext(Dispatchers.IO) {
            if (!surveyData.isFeasible) return@withContext null
            val surveyIdPart =
                "${surveyData.batchNum.trim()}_${surveyData.intraBatchId.trim()}"
                    .padEnd(15,'_')
                    .toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())

            val userIdPart = userId
                .toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
            //Json part
            val jsonPart = GSON.toJson(surveyData)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            //Image part
            val reasonImage: File = surveyData.reasonImage!!

            val filePart = MultipartBody.Part.createFormData(
                "reasonImage",
                reasonImage.name,
                reasonImage
                    .asRequestBody("image/*".toMediaTypeOrNull())
            )
            val response = if (surveyData.hasAdditionalNotes) {
                val extraImage: File = surveyData.extraImage!!
                val extraFilePart = MultipartBody.Part.createFormData(
                    "additionalImage",
                    extraImage.name,
                    extraImage
                        .asRequestBody("image/*".toMediaTypeOrNull())
                )
                service.uploadFormWithExtraImage(
                    bearerToken,
                    surveyIdPart, userIdPart,
                    jsonPart,
                    filePart, extraFilePart
                )
            }else {
                service.uploadFormNoExtraImage(
                    bearerToken,
                    surveyIdPart, userIdPart,
                    jsonPart,
                    filePart
                )
            }
            when (response) {
                is NetworkResponse.Success -> return@withContext response.body.id
                is NetworkResponse.Error -> {
                    Log.d("debug error", response.error.toString())
                    return@withContext null
                }
            }
        }
    }
}