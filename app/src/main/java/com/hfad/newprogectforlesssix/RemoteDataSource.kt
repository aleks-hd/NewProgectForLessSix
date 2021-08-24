package com.hfad.newprogectforlesssix

import com.google.gson.GsonBuilder
import com.hfad.newprogectforlesssix.model.Weather
import retrofit2.Callback

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val REQUEST_API_KEY = "X-Yandex-API-Key"

// Источник данных
class RemoteDataSource {
    private val weatherAPI = Retrofit.Builder()
        .baseUrl("https://api.weather.yandex.ru/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(WeatherAPI::class.java)

    fun getWeatherDetails(lat: Double, lon: Double, callback: Callback<Weather>) {
        weatherAPI.getWeather(
            "5682b28c-5134-4a21-ad13-9fb93d2399fd",
            lat, lon
        ).enqueue(callback)
    }
}