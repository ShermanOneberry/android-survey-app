package com.oneberry.survey_report_app.network

import android.graphics.BitmapFactory
import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.oneberry.survey_report_app.data.StoredImage
import com.oneberry.survey_report_app.data.SurveyReport
import com.oneberry.survey_report_app.network.api_body.AuthApiData
import com.oneberry.survey_report_app.network.api_body.GetSubmissionsApiBody
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
    private val apiUrl = apiUrl
    private val GSON = GsonFactory().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(GSON))
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
            val reasonImage: File? = surveyData.reasonImage

            val filePart = if (reasonImage != null) {
                MultipartBody.Part.createFormData(
                    "reasonImage",
                    reasonImage.name,
                    reasonImage
                        .asRequestBody("image/*".toMediaTypeOrNull())
                )
            } else {
                val uneditedImage = surveyData.editOnlyData!!.reasonImage
                MultipartBody.Part.createFormData(
                    "reasonImage",
                    uneditedImage.filename,
                    uneditedImage.byteArray
                        .toRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            val response = if (surveyData.hasAdditionalNotes) {
                val extraImage: File? = surveyData.extraImage
                val extraFilePart = if (extraImage != null) {
                    MultipartBody.Part.createFormData(
                        "additionalImage",
                        extraImage.name,
                        extraImage
                            .asRequestBody("image/*".toMediaTypeOrNull())
                    )
                } else {
                    val uneditedImage = surveyData.editOnlyData!!.extraImage!!
                    MultipartBody.Part.createFormData(
                        "reasonImage",
                        uneditedImage.filename,
                        uneditedImage.byteArray
                            .toRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                if (surveyData.editOnlyData != null) {
                    service.updateFormWithExtraImage(
                        bearerToken,
                        surveyData.editOnlyData.recordId,
                        surveyIdPart, userIdPart,
                        jsonPart,
                        filePart, extraFilePart
                    )
                } else {
                    service.uploadFormWithExtraImage(
                        bearerToken,
                        surveyIdPart, userIdPart,
                        jsonPart,
                        filePart, extraFilePart
                    )
                }
            }else {
                if (surveyData.editOnlyData != null) {
                    service.updateFormNoExtraImage(
                        bearerToken,
                        surveyData.editOnlyData.recordId,
                        surveyIdPart, userIdPart,
                        jsonPart,
                        filePart
                    )
                } else {
                    service.uploadFormNoExtraImage(
                        bearerToken,
                        surveyIdPart, userIdPart,
                        jsonPart,
                        filePart
                    )
                }
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

    suspend fun getPastSubmissionsList(
        bearerToken: String,
        block: String,
        street: String,
        page: Int,
    ): GetSubmissionsApiBody? {
        return withContext(Dispatchers.IO) {
            val response = service.getPastSubmissions(
                bearerToken,
                page,
                sort = "-updated,-surveyRequest",
                filter =
                    "surveyRequest.block ~ \"$block\" && " +
                    "surveyRequest.streetName ~ \"$street\"",
                expand = "surveyRequest",
            )
            when (response) {
                is NetworkResponse.Success -> return@withContext response.body
                is NetworkResponse.Error -> {
                    Log.d("debug error", response.error.toString())
                    return@withContext null
                }
            }
        }
    }
    suspend fun getMaxBatchNumber(
        bearerToken: String
    ): Int? {
        return withContext(Dispatchers.IO) {
            when (val response = service.getMaxBatchNum(bearerToken)) {
                is NetworkResponse.Success -> {
                    val firstItem = response.body.items.getOrNull(0)
                    return@withContext firstItem?.batchNumber ?: 0
                }
                is NetworkResponse.Error -> {
                    return@withContext null
                }
            }
        }
    }
    suspend fun getImage(
        bearerToken: String,
        collectionId: String,
        recordId: String,
        imageFilename: String,
    ): StoredImage? {
        return withContext(Dispatchers.IO) {
            val fileToken = when(
                val response = service.getFileToken(bearerToken)
            ) {
                is NetworkResponse.Error -> {
                    return@withContext null
                }
                is NetworkResponse.Success -> {
                    response.body.token
                }
            }
            val imageUrl =
                "$apiUrl/api/files/$collectionId/$recordId/$imageFilename" +
                        "?token=$fileToken"
            val body = service.fetchImage(imageUrl).execute().body() ?: return@withContext null
            val bytes = body.bytes()

            val baseName = imageFilename.substringBeforeLast('_')
            val extension = imageFilename.substringAfterLast('.')
            val originalFileName = "$baseName.$extension"

            return@withContext StoredImage (
                bytes,
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size),
                originalFileName,
            )
        }
    }
}