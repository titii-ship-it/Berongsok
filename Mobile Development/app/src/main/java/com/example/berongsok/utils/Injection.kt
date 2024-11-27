package com.example.berongsok.utils

import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.api.ApiConfig

object Injection {
    fun provideUserRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService)
    }
}