package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

    @field:SerializedName("data")
    val data: List<DataItem?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataItem(

    @field:SerializedName("imgUrl")
    val imgUrl: String? = null,

    @field:SerializedName("wasteType")
    val wasteType: String? = null,

    @field:SerializedName("tpsId")
    val tpsId: String? = null,

    @field:SerializedName("totalPrice")
    val totalPrice: Int? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("weight")
    val weight: Int? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("nasabahName")
    val nasabahName: String? = null,

    @field:SerializedName("transactionId")
    val transactionId: String? = null,

    @field:SerializedName("createAt")
    val createAt: String? = null
)
