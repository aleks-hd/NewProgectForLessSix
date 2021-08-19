package com.hfad.newprogectforlesssix

import com.hfad.newprogectforlesssix.model.Weather

sealed class AppState {
    data class Success(val weather: Weather) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
