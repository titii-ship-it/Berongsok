package com.example.berongsok.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.response.LoginResponse
import com.example.berongsok.data.remote.response.OTPResponse
import com.example.berongsok.data.remote.response.ResetPasswordResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _otpResult = MutableLiveData<Result<OTPResponse>>()
    val otpResult: LiveData<Result<OTPResponse>> = _otpResult

    private val _resetResult = MutableLiveData<Result<ResetPasswordResponse>>()
    val resetResult: LiveData<Result<ResetPasswordResponse>> = _resetResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = authRepository.login(email, password)
                _loginResult.postValue(response)
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun sendOTP(email: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = authRepository.sendOTP(email)
                _otpResult.postValue(response)
            } catch (e: Exception) {
                _otpResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun resetPassword(email: String, otp: Int, newPassword: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = authRepository.resetPassword(email, otp, newPassword)
                _resetResult.postValue(response)
            } catch (e: Exception) {
                _resetResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }
}

class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}