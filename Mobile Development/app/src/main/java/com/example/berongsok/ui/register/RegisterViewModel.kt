package com.example.berongsok.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = authRepository.register(username, email, password)
                _registerResult.postValue(response)
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }
}

class RegisterViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}