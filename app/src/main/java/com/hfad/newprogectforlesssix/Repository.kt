package com.hfad.newprogectforlesssix

import com.hfad.newprogectforlesssix.model.Weather

interface Repository {
    fun getDataFromServer() : Weather
}