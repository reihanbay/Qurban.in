package com.dicoding.qurbanin.data.api

import com.dicoding.qurbanin.data.model.DistrictResponse
import com.dicoding.qurbanin.data.model.ProvinceResponseItem
import com.dicoding.qurbanin.data.model.RegencyResponse
import com.dicoding.qurbanin.data.model.VillageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationServices {
    @GET("provinces.json")
    suspend fun getProvince() : List<ProvinceResponseItem>

    @GET("regencies/{provinceId}.json")
    suspend fun getRegency(@Path("provinceId") id : String) : List<RegencyResponse>

    @GET("districts/{regencyId}.json")
    suspend fun getDistricts(@Path("regencyId") id : String) : List<DistrictResponse>

    @GET("villages/{districtId}.json")
    suspend fun getVillages(@Path("districtId") id: String) : List<VillageResponse>
}