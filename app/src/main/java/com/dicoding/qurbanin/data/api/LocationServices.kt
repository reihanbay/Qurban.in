package com.dicoding.qurbanin.data.api

import com.dicoding.qurbanin.data.response.DistrictResponse
import com.dicoding.qurbanin.data.response.ProvinceResponseItem
import com.dicoding.qurbanin.data.response.RegencyResponse
import com.dicoding.qurbanin.data.response.VillageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationServices {
    @GET("provinces.json")
    suspend fun getProvince() : List<ProvinceResponseItem>

    @GET("regencies/{provinceId}.json")
    suspend fun getRegency(@Path("provinceId") id : Int) : List<RegencyResponse>

    @GET("districts/{regencyId}.json")
    suspend fun getDistricts(@Path("regencyId") id : Int) : List<DistrictResponse>

    @GET("villages/{districtId}.json")
    suspend fun getVillages(@Path("districtId") id: Int) : List<VillageResponse>
}