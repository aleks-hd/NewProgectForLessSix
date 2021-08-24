package com.hfad.newprogectforlesssix

import com.hfad.newprogectforlesssix.model.Weather
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherAPI {
    @GET("v2/informers")
    fun getWeather(
        @Header("X-Yandex-API-Key") token:String,
        @Query("lat") lat:Double,
        @Query("lon") lon: Double

    ): Call<Weather>
}