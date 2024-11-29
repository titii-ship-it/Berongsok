package com.example.berongsok.data.remote

import com.example.berongsok.data.remote.api.ApiService
import com.example.berongsok.data.remote.response.ErrorResponse
import com.example.berongsok.data.remote.response.HistoryResponse
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.NewTransactionResponse
import com.example.berongsok.data.remote.response.PredictResponse
import com.example.berongsok.data.remote.response.RegisterResponse
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService) {
    suspend fun register(username: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(username, email, password)
            Result.success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: "An unknown error occurred"

            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Result.success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: "An unknown error occurred"

            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun uploadPhoto(token: String, multipartBody: MultipartBody.Part): Result<PredictResponse> {
        return try {
            val response = apiService.uploadPhoto(token, multipartBody)
            Result.success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: "An unknown error occurred"
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun addTransaction(
        token: String,
        nasabahName: RequestBody,
        wasteType: RequestBody,
        price: RequestBody,
        weight: RequestBody,
        totalPrice: RequestBody,
        image: MultipartBody.Part
    ): Result<NewTransactionResponse> {
        return try {
            val response = apiService.addTransaction(
                token,
                nasabahName,
                wasteType,
                price,
                weight,
                totalPrice,
                image
            )
            Result.success(response)
        } catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message ?: "An unknown error occurred"
            Result.failure(Exception(errorMessage))
        }
    }

//    suspend fun getHistory(tpsId :String): Result<HistoryResponse> {
//        return try {
//            val response = apiService.getHistory(tpsId)
//            Result.success(response)
//        } catch (e: HttpException) {
//            val errorBodyString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
//            val errorMessage = errorBody?.message ?: "An unknown error occurred"
//            Result.failure(Exception(errorMessage))
//        }
//
//    }
}