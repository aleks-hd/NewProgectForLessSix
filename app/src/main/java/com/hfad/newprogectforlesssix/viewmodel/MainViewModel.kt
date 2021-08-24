package com.hfad.newprogectforlesssix.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.newprogectforlesssix.AppState
import com.hfad.newprogectforlesssix.RemoteDataSource
import com.hfad.newprogectforlesssix.RepositoryImp
import com.hfad.newprogectforlesssix.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel(
    private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData(),
    private val detailRepositoryIml: RepositoryImp = RepositoryImp(RemoteDataSource())
) :
    ViewModel() {

    fun getData()= liveDataToObserver


    fun getWeatherFromServer(lat:Double, lon:Double) {
        liveDataToObserver.value = AppState.Loading
        detailRepositoryIml.getWeatherFromServer(lat,lon, callback)
    }
    private val callback = object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
            val serverResponse: Weather? = response.body()
            liveDataToObserver.postValue(
                if( response.isSuccessful && response.body() !=null){
                    checkResponse(serverResponse)
                }else{
                    AppState.Error(Throwable("ОШИБКА СО СТОРОНЫ СЕРВЕРА"))
                }
            )
        }

        override fun onFailure(call: Call<Weather>, t: Throwable) {
            TODO("Not yet implemented")
        }

    }

    private fun checkResponse(serverResponse: Weather?): AppState {
            val fact = serverResponse?.fact
        val info = serverResponse?.info
            return if (fact ==null || info == null){
                AppState.Error(Throwable("SERVER ERROR"))
            } else{
                AppState.Success(serverResponse)
            }
    }


}