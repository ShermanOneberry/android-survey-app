package com.oneberry.survey_report_app.network


import com.haroldadmin.cnradapter.NetworkResponse
import com.oneberry.survey_report_app.network.api_body.AuthApiBody
import com.oneberry.survey_report_app.network.api_body.GetFileTokenApiBody
import com.oneberry.survey_report_app.network.api_body.GetMaxBatchNumApiBody
import com.oneberry.survey_report_app.network.api_body.GetSubmissionsApiBody
import com.oneberry.survey_report_app.network.api_body.PostFormApiBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url


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
        @Part("surveyRequest") surveyID: RequestBody,
        @Part("assignedUser") userID: RequestBody,
        @Part("formData") formData: RequestBody,
        @Part filePart: MultipartBody.Part
    ): NetworkResponse<PostFormApiBody, ErrorBody>@Multipart

    @POST("api/collections/surveyResults/records")
    suspend fun uploadFormWithExtraImage(
        @Header("Authorization") bearerToken: String,
        @Part("surveyRequest") surveyID: RequestBody,
        @Part("assignedUser") userID: RequestBody,
        @Part("formData") formData: RequestBody,
        @Part filePart1: MultipartBody.Part,
        @Part filePart2: MultipartBody.Part
    ): NetworkResponse<PostFormApiBody, ErrorBody>

    @GET("api/collections/surveyResults/records")
    suspend fun getPastSubmissions(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int,
        // @Query("perPage") perPage: Int,
        @Query("sort") sort: String,
        @Query("filter") filter: String,
        @Query("expand") expand: String,

    ): NetworkResponse<GetSubmissionsApiBody, ErrorBody>

    @GET("/api/collections/surveyDetails/records?page=1&sort=-batchNumber&perPage=1")
    suspend fun getMaxBatchNum(
        @Header("Authorization") bearerToken: String,
    ): NetworkResponse<GetMaxBatchNumApiBody, ErrorBody>

    @POST("/api/files/token")
    suspend fun getFileToken(
        @Header("Authorization") bearerToken: String,
    ): NetworkResponse<GetFileTokenApiBody, ErrorBody>

    @GET
    fun fetchImage(@Url url: String?): Call<ResponseBody>
}