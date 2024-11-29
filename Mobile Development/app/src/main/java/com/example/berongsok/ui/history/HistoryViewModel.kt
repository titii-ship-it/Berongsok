package com.example.berongsok.ui.history

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.api.ApiConfig
import com.example.berongsok.data.remote.response.DataItem
import com.example.berongsok.data.remote.response.HistoryResponse
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.ui.login.LoginViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel(private val dataStoreManager: SettingPreferences) : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "Transaction History"
//    }
//    val text: LiveData<String> = _text

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _historyResult = MutableLiveData<Result<HistoryResponse>>()
    val historyResult: LiveData<Result<HistoryResponse>> = _historyResult

    private val _result = MutableLiveData<List<DataItem?>?>()
    val result: LiveData<List<DataItem?>?> = _result


    fun getStories() {
        viewModelScope.launch {
            dataStoreManager.tpsId.collect { tpsId ->
                if (!tpsId.isNullOrEmpty()) {
//                    _loading.value = true
                    val client = ApiConfig.getApiService().getHistory(tpsId)
                    client.enqueue(object : Callback<HistoryResponse> {
                        override fun onResponse(
                            call: Call<HistoryResponse>,
                            response: Response<HistoryResponse>
                        ) {
                            if (response.isSuccessful) {
//                                _isError.value = false
//                                _isLoading.value = false
                                Log.d(ContentValues.TAG, "Response successful: ${response.body()}")
                                _result.value = response.body()?.data
                            } else {
//                                _isError.value = true
//                                _isLoading.value = false
                                Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
//                            _isError.value = true
//                            _isLoading.value = false
                            Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                        }
                    })
                } else {
//                    _isError.value = true
//                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "Token is null or empty")
                }
            }
        }
    }

//    suspend fun getHistory() {
//        dataStoreManager.tpsId.collect() { tpsId ->
//            viewModelScope.launch {
//                try {
//                    val response: Result<HistoryResponse>? = tpsId?.let {
//                        authRepository.getHistory(
//                            it
//                        )
//                    }
//                    _historyResult.value = response.data?.filterNotNull() ?: emptyList()
//                } catch (e: Exception) {
//                    _error.value = e.message
//                } finally {
//                    _loading.value = false
//                }
//            }
//        }
//    }



//    suspend fun getHistory() {
//        dataStoreManager.tpsId.collect() { tpsId ->
//            if (!tpsId.isNullOrEmpty()) {
//                Log.d("tps id ", " $tpsId")
//                try {
//                    val response = authRepository.getHistory(tpsId)
//                    _historyResult.postValue(response)
//                } catch (e: Exception) {
//                    Log.e("Add Transaction Error", e.message ?: "Unknown error")
//                    _historyResult.postValue(Result.failure(e))
//                }
//            } else {
//                Log.e("Token Error", "Authorization token is missing or empty.")
//            }
//        }
//    }

//    fun getStories() {
//        viewModelScope.launch {
//            dataStoreManager.tpsId.collect {tpsId ->
//                if (!tpsId.isNullOrEmpty()) {
//                    _loading.value = true
//                    val client = ApiConfig.getApiService().getHistory(tpsId).data
//                    client.enqueue(object : Callback<StoryResponse> {
//                        override fun onResponse(
//                            call: Call<StoryResponse>,
//                            response: Response<StoryResponse>
//                        ) {
//                            if (response.isSuccessful) {
//                                _isError.value = false
//                                _isLoading.value = false
//                                Log.d(ContentValues.TAG, "Response successful: ${response.body()}")
//                                _stories.value = response.body()?.listStory ?: listOf()
//                            } else {
//                                _isError.value = true
//                                _isLoading.value = false
//                                Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
//                            }
//                        }
//
//                        override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                            _isError.value = true
//                            _isLoading.value = false
//                            Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
//                        }
//                    })
//                } else {
//                    _isError.value = true
//                    _isLoading.value = false
//                    Log.e(ContentValues.TAG, "Token is null or empty")
//                }
//            }
//        }
//    }
}

class HistoryViewModelFactory(private val dataStoreManager: SettingPreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
