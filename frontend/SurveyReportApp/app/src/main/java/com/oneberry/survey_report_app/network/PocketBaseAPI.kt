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
    suspend fun uploadForm(
        @Header("Authorization") bearerToken: String,
        @Part("surveyRequest") surveyID: RequestBody,
        @Part("formData") formData: RequestBody,
        @Part filePart: MultipartBody.Part
    ): NetworkResponse<PostFormApiBody, ErrorBody>

}