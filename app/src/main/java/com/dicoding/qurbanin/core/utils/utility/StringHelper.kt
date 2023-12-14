package com.dicoding.qurbanin.core.utils.utility

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
}