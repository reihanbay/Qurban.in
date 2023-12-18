package com.dicoding.qurbanin.data.model


data class EventRegisteredResponse(
	val idRegistered : String,
	val model : EventRegisteredResponseItem?
)
data class EventRegisteredResponseItem(

	val idstock: String,

	val statusStock: String,

	val pelaksanaan: String,

	val idevent: String,

	val nameRegister: String,

	val pengambilan: String,

	val uid_User: String,

	val location: String,

	val name: String,

	var rekening: String? = null
) {
	constructor() : this("","","","","","","","","","")
}

