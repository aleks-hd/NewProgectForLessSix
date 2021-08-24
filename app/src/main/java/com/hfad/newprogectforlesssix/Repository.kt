package com.hfad.newprogectforlesssix

import com.hfad.newprogectforlesssix.model.Weather

interface Repository {
    fun getWeatherFromServer(lat:Double, lon:Double, callback: retrofit2.Callback<Weather>)
}