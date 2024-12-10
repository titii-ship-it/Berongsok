package com.example.berongsok.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.remote.api.ApiConfig
import com.example.berongsok.data.remote.response.DataItem
import com.example.berongsok.data.remote.response.HistoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _historyData = MutableLiveData<List<DataItem?>?>()
    val historyData: LiveData<List<DataItem?>?> get() = _historyData

    fun fetchHistoryData(tpsId: String) {
        val apiService = ApiConfig.getApiService()
        _loading.value = true
        viewModelScope.launch {
            apiService.getHistory(tpsId).enqueue(object : Callback<HistoryResponse> {
                override fun onResponse(
                    call: Call<HistoryResponse>,
                    response: Response<HistoryResponse>
                ) {
                    if (response.isSuccessful) {
                        _historyData.value = response.body()?.data
                        Log.d("TAG", "onResponse cek data: ${response.body()?.data}")
                        Log.d("HistoryViewModel", "onResponse: sukses $response ")
                        _loading.value = false
                    } else {
                        // Handle error
                        _historyData.value = emptyList()
                        Log.e("HistoryViewModel", "Error fetching history: ${response.message()}")
                        _loading.value = false
                    }
                }

                override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                    _loading.value = false
                    Log.e("HistoryViewModel", "Error: ${t.message}")
                    _historyData.value = emptyList()
                }
            })
        }
    }
}

class HistoryViewModelFactory :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
