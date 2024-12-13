package com.example.berongsok.ui.historydetail

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

class HistoryDetailViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _historyDetailData = MutableLiveData<List<DataItem?>?>()
    val historyDetailData: LiveData<List<DataItem?>?> get() = _historyDetailData

    fun fetchHistoryDetailData(tpsId: String?, transactionId: String) {
        // Contoh menggunakan Retrofit untuk memanggil API
        val apiService = ApiConfig.getApiService()
        _loading.value = true
        viewModelScope.launch {
            if (tpsId != null) {
                apiService.getDetailHistory(tpsId, transactionId)
                    .enqueue(object : Callback<HistoryResponse> {
                        override fun onResponse(
                            call: Call<HistoryResponse>,
                            response: Response<HistoryResponse>
                        ) {
                            if (response.isSuccessful) {
                                _historyDetailData.value = response.body()?.data
                                Log.d("TAG", "onResponse cek data: ${response.body()?.data}")
                                Log.d("HistoryDetailViewModel", "onResponse: sukses $response ")
                                Log.d(
                                    "HistoryDetailViewModel",
                                    "Fetching details with tpsId: $tpsId, transactionId: $transactionId"
                                )

                                _loading.value = false
                            } else {
                                // Handle error
                                _historyDetailData.value = emptyList()
                                Log.e(
                                    "HistoryDetailViewModel",
                                    "Error fetching history: ${response.message()}"
                                )
                                _loading.value = false

                            }
                        }

                        override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                            // Handle failure
                            _loading.value = false
                            Log.e("HistoryViewModel", "Error: ${t.message}")
                            _historyDetailData.value = emptyList()
                        }
                    })
            }
        }
    }
}

class HistoryDetailViewModelFactory :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryDetailViewModel::class.java)) {
            return HistoryDetailViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}