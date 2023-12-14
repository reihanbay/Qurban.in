package com.dicoding.qurbanin.data.model

import com.google.gson.annotations.SerializedName

data class ProvinceResponseItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)

data class RegencyResponse(

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)

data class DistrictResponse(

	@field:SerializedName("regency_id")
	val regencyId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)

data class VillageResponse(

	@field:SerializedName("district_id")
	val districtId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
