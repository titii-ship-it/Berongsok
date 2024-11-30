package com.example.berongsok.data.remote.api

import com.example.berongsok.data.remote.response.HistoryResponse
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.NewTransactionResponse
import com.example.berongsok.data.remote.response.PredictResponse
import com.example.berongsok.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
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
        @Part("nasabahName") nasabahName: String,
        @Part("wasteType") wasteType: String,
        @Part("price") price: Int,
        @Part("weight") weight: String,
        @Part("totalPrice") totalPrice: Int,
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


}