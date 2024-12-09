package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class OTPResponse (
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("message")
    val message: String
)

data class ResetPasswordResponse (
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("message")
    val message: String
)