package com.oneberry.survey_report_app.network


import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface PocketBaseAPI {

    @FormUrlEncoded
    @POST("api/collections/users/auth-with-password")
    suspend fun getBearerToken(
        @Field("identity") identity: String,
        @Field("password") password: String
    ): NetworkResponse<AuthApiBody, ErrorBody>

    @Multipart
    @POST("api/collections/surveyResults/records")
    suspend fun uploadFormNoExtraImage(
        @Header("Authorization") bearerToken: String,
        //TODO: Add user ID (Surveys are not preassigned)
        @Part("surveyRequest") surveyID: RequestBody,
        @Part("assignedUser") userID: RequestBody,
        @Part("formData") formData: RequestBody,
        @Part filePart: MultipartBody.Part
    ): NetworkResponse<PostFormApiBody, ErrorBody>@Multipart

    @POST("api/collections/surveyResults/records")
    suspend fun uploadFormWithExtraImage(
        @Header("Authorization") bearerToken: String,
        //TODO: Add user ID (Surveys are not preassigned)
        @Part("surveyRequest") surveyID: RequestBody,
        @Part("assignedUser") userID: RequestBody,
        @Part("formData") formData: RequestBody,
        @Part filePart1: MultipartBody.Part,
        @Part filePart2: MultipartBody.Part
    ): NetworkResponse<PostFormApiBody, ErrorBody>

}