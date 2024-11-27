package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: Tps
)

data class Tps (
    @field:SerializedName("tpsId")
    val tpsId: String,

    @field:SerializedName("username")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("token")
    val token: String,
)