package com.dicoding.qurbanin.data.model

data class ListEventQurbanResponseItem(

	val IDStock: String,

	val StatusStock: String,

	val Pelaksanaan: String,

	val IDEvent: String,

	val NameRegister: String,

	val Pengambilan: String,

	val UID_User: String,

	val Location: String,

	val Name: String,

	val StatusEvent: String,

	var rekening: String? = null
) {
	constructor() : this("","","","","","","","","","")
}

