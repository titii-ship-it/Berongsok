package com.example.berongsok.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.response.DashboardResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dataStoreManager: SettingPreferences,
    private val userRepository: AuthRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _dashboardResult = MutableLiveData<Result<DashboardResponse>>()
    val dashboardResult: LiveData<Result<DashboardResponse>> = _dashboardResult

    init {
        getDashboardData()
    }

    fun getDashboardData() {

        viewModelScope.launch {
            _loading.value = true

            val token = dataStoreManager.tpsToken.firstOrNull()
            if (!token.isNullOrEmpty()) {
                try {
                    val response = userRepository.getDashboardData("Bearer $token")
                    Log.d("cek token dashboard", "getDashboardData token: $token")
                    _dashboardResult.value = response
                    Log.d("dashboardviewmodel", "getDashboardData: $response")
                } catch (e: Exception) {
                    Log.e("Fetch Dashboard Data", e.message ?: "Unknown error")
                    _dashboardResult.value = Result.failure(e)
                }
            } else {
                Log.e("Token Error", "Authorization token is missing or empty.")
            }
        }
    }
}

class DashboardViewModelFactory(
    private val dataStoreManager: SettingPreferences,
    private val userRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(dataStoreManager, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}