package com.dicoding.qurbanin.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(

	val Alamat: String,

	val Nama: String,

	val Tipe: String
) {
	constructor() : this("", "", "")
}
