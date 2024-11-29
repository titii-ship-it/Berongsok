package com.example.berongsok.ui.transaction

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.remote.AuthRepository
import com.example.berongsok.data.remote.response.NewTransactionResponse
import com.example.berongsok.data.remote.response.PredictResponse
import com.example.berongsok.ui.scan.ScanViewModel
import com.example.berongsok.utils.reduceFileImage
import com.example.berongsok.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TransactionViewModel (private val dataStoreManager: SettingPreferences, private val userRepository: AuthRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Scan Waste"
    }
    val text: LiveData<String> = _text

    private val _addTransaction = MutableLiveData<Result<NewTransactionResponse>>()
    val addResult: LiveData<Result<NewTransactionResponse>> = _addTransaction

    fun addTransaction(
        imageUri: Uri,
        nasabahName: String,
        wasteType: String,
        price: String,
        weight: String,
        totalPrice: String,
        context: Context
    ) {
        imageUri.let { uri ->
            val imageFile = uriToFile(uri, context).reduceFileImage()
            Log.d("Image File", "Path: ${imageFile.path}")
            val requestImageFile = imageFile.asRequestBody("image/png".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )

            val nasabahNamePart = nasabahName.toRequestBody("text/plain".toMediaType())
            val wasteTypePart = wasteType.toRequestBody("text/plain".toMediaType())
            val pricePart = price.toRequestBody("text/plain".toMediaType())
            val weightPart = weight.toRequestBody("text/plain".toMediaType())
            val totalPricePart = totalPrice.toRequestBody("text/plain".toMediaType())

            viewModelScope.launch {
                dataStoreManager.tpsToken.collect { token ->
                    if (!token.isNullOrEmpty()) {
                        Log.d("Authorization Token", "Bearer $token")
                        try {
                            val response = userRepository.addTransaction(
                                "Bearer $token",
                                nasabahNamePart,
                                wasteTypePart,
                                pricePart,
                                weightPart,
                                totalPricePart,
                                multipartBody
                            )
                            _addTransaction.postValue(response)
                        } catch (e: Exception) {
                            Log.e("Add Transaction Error", e.message ?: "Unknown error")
                            _addTransaction.postValue(Result.failure(e))
                        }
                    } else {
                        Log.e("Token Error", "Authorization token is missing or empty.")
                    }
                }
            }
        }
    }


}

class TransactionViewModelFactory(private val dataStoreManager: SettingPreferences, private val userRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(dataStoreManager, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}