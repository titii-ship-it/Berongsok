package com.example.berongsok.data.remote

import com.example.berongsok.data.remote.api.ApiService
import com.example.berongsok.data.remote.response.ErrorResponse
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.RegisterResponse
import com.google.gson.Gson
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
}