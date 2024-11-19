package com.example.berongsok.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Scan Fragment"
    }
    val text: LiveData<String> = _text
}