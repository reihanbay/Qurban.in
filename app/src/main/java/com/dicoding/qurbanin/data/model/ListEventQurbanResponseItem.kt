package com.dicoding.qurbanin.data.model

import com.google.gson.annotations.SerializedName

data class ListEventQurbanResponseItem(

	val IDStock: String,

	val StatusStock: String ,

	val Pelaksanaan: String,

	@field:SerializedName("IDEvent")
	val IDEvent: String,

	@field:SerializedName("NameRegister")
	val NameRegister: String,

	@field:SerializedName("Pengambilan")
	val Pengambilan: String,

	@field:SerializedName("UID_User")
	val UID_User: String,

	@field:SerializedName("Location")
	val Location: String,

	@field:SerializedName("Name")
	val Name: String,

	@field:SerializedName("StatusEvent")
	val StatusEvent: String
) {
	constructor() : this("","","","","","","","","","")
}

