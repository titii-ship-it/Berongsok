package com.example.berongsok.ui.scan

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
import com.example.berongsok.data.remote.response.PredictResponse
import com.example.berongsok.utils.reduceFileImage
import com.example.berongsok.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ScanViewModel(private val dataStoreManager: SettingPreferences, private val userRepository: AuthRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Scan Waste"
    }
    val text: LiveData<String> = _text

    private val _uploadResult = MutableLiveData<Event<Result<PredictResponse>>>()
    val uploadResult: LiveData<Event<Result<PredictResponse>>> = _uploadResult

    fun uploadStory(imageUri: Uri, context: Context) {
        imageUri.let { uri ->
            val imageFile = uriToFile(uri, context).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val requestImageFile = imageFile.asRequestBody("image/png".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )
            viewModelScope.launch {
                dataStoreManager.tpsToken.collect { token ->
                    if (!token.isNullOrEmpty()) {
                        Log.d("Image Path", imageFile.path)
                        Log.d("Image Size", imageFile.length().toString())
                        Log.d("MIME Type", requestImageFile.contentType().toString())
                        Log.d("Header Token", "Bearer $token")
                        Log.d("Multipart Part", multipartBody.toString())
                        try {
                            val response = userRepository.uploadPhoto("Bearer $token", multipartBody)
                            _uploadResult.postValue(Event(response))
                        } catch (e: Exception) {
                            _uploadResult.postValue(Event(Result.failure(e)))
                        }
                    }
                }
            }
        }
    }
}

class ScanViewModelFactory(private val dataStoreManager: SettingPreferences, private val userRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(dataStoreManager, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}