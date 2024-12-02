package com.example.berongsok.data.remote.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(

	@field:SerializedName("data")
	val data: List<DashboardDataItem?>? = null,

	@field:SerializedName("tpsId")
	val tpsId: String? = null,

	@field:SerializedName("totalPrice")
	val totalPrice: Int? = null,

	@field:SerializedName("totalWeight")
	val totalWeight: Double? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class DashboardDataItem(

	@field:SerializedName("wasteType")
	val wasteType: String? = null,

	@field:SerializedName("totalPrice")
	val totalPrice: Int? = null,

	@field:SerializedName("totalWeight")
	val totalWeight: Double? = null
)
