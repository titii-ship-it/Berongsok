package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class NewTransactionResponse (
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)