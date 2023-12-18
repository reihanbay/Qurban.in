package com.dicoding.qurbanin.core.utils.utility

import android.content.Context
import android.content.res.ColorStateList
import com.dicoding.qurbanin.R
import java.util.Calendar

object StringHelper {
    fun concatLocation(
        province: String,
        regency: String,
        district: String,
        village: String,
    ): String {
        val modifyProvince = modifyString(province)
        val modifyRegency = modifyString(regency)
        val modifyDistrict = modifyString(district)
        val modifyVillage = modifyString(village)

        return "$modifyVillage, $modifyDistrict, $modifyRegency, $modifyProvince"
    }

    private fun modifyString(name: String) = name.split(" ").joinToString(" ") {
        it.lowercase().replaceFirstChar { char -> char.uppercase() }
    }

    fun greeting(name: String): String {
        val calendar = Calendar.getInstance()
        val greet = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 4.until(11) -> "Selamat Pagi, "
            in 11.until(15) -> "Selamat Siang, "
            in 15.until(19) -> "Selamat Sore, "
            else -> "Selamat Malam, "
        }
        return greet + name
    }

    fun styleTextViewStatus(context: Context, status:String) : Pair<ColorStateList, String> =
        when (status) {
            "unpaid" -> ColorStateList.valueOf(context.getColor(R.color.yellow))
            "sold","available" -> ColorStateList.valueOf(context.getColor(R.color.green_40))
            "ongoing" -> ColorStateList.valueOf(context.getColor(R.color.blue))
            "execute" -> ColorStateList.valueOf(context.getColor(R.color.purple))
            "cancel" -> ColorStateList.valueOf(context.getColor(R.color.red))
            else -> ColorStateList.valueOf(context.getColor(R.color.grey_20))
        } to
        when (status) {
            "unpaid" -> context.getString(R.string.unpaid)
            "available" -> context.getString(R.string.available)
            "sold" -> context.getString(R.string.sold)
            "ongoing" -> context.getString(R.string.ongoing)
            "execute" -> context.getString(R.string.execute)
            "cancel" -> context.getString(R.string.cancel)
            else -> context.getString(R.string.deliver)
        }
}