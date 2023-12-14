package com.dicoding.qurbanin.data.model


data class EventQurbanResponse(
	var idEvent : String = "",
	var data : DataEventItem? = null,
	var stock : List<StockDataResponse>? = null,
)
data class DataEventItem(
	val Lokasi: String,
	val UID: String,
	val Pelaksanaan: String,
	val Pengambilan: String,
	val Rekening: String,
	val Name: String,
)  {
	constructor() : this("","" ,"","","","")
}


