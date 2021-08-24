package com.hfad.newprogectforlesssix

import com.hfad.newprogectforlesssix.model.Weather
import retrofit2.Callback


class RepositoryImp(private val remoteDataSource: RemoteDataSource) : Repository {
    override fun getWeatherFromServer(lat: Double, lon: Double, callback: Callback<Weather>) {
        remoteDataSource.getWeatherDetails(lat, lon, callback)
    }
}



