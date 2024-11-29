package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse (
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val result: PredictResult,
)

data class PredictResult (
    @field:SerializedName("tpsId")
    val tpsId: String,

    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("confidenceScore")
    val score: Double,

    @field:SerializedName("price")
    val price: String,

    @field:SerializedName("createAt")
    val createAt: String,
)