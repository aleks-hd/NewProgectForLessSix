package com.hfad.newprogectforlesssix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.newprogectforlesssix.AppState


class MainViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()
/*private val repositotyImp :RepositoryImp = RepositoryImp()*/
) :
    ViewModel() {

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getWeatherFromServer() {
        getLiveDataSources()
    }

    private fun getLiveDataSources() {
        Thread {
            //liveDataToObserver.postValue(AppState.Success(repositotyImp.getDataFromServer()))
        }.start()
    }

}