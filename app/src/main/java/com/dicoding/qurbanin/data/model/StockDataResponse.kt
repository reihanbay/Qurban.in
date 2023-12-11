package com.dicoding.qurbanin.data.model

import com.google.gson.annotations.SerializedName



data class StockDataResponse(
	val keyItem : String,
	val dataItem : StockDataItem?,
	val SoldBy: List<SoldByItem>?

)
data class StockDataItem(
	val Type: String,

	val Price: String,

	val StatusStock: String,

) {
	constructor() : this("", "", "")

}
data class SoldByItem(
	val idUser : String ,
	val name : String
)


