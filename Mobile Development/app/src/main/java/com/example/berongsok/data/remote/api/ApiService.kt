package com.example.berongsok.data.remote.api

import com.example.berongsok.data.remote.response.DashboardResponse
import com.example.berongsok.data.remote.response.HistoryResponse
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.NewTransactionResponse
import com.example.berongsok.data.remote.response.PredictResponse
import com.example.berongsok.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("verify-registration")
    suspend fun verifyAccount(
        @Field("email") email: String,
        @Field("otp") otp: Int
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("predict")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
    ): PredictResponse

    @Multipart
    @POST("waste/transactions")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Part("nasabahName") nasabahName: RequestBody,
        @Part("wasteType") wasteType: RequestBody,
        @Part("price") price: Int,
        @Part("weight") weight: Double,
        @Part("totalPrice") totalPrice: Double,
        @Part image: MultipartBody.Part,
    ): NewTransactionResponse

    @GET("transactionhistory")
    fun getHistory(
        @Query("tpsId") tpsId: String
    ): Call<HistoryResponse>

    @GET("transactionhistory")
    fun getDetailHistory(
        @Query("tpsId") tpsId: String,
        @Query("transactionId") transactionId: String
    ): Call<HistoryResponse>

    @GET("dashboard")
    suspend fun getDashboardData(
        @Header("Authorization") token: String
    ): DashboardResponse


}