package com.hfad.newprogectforlesssix

import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.hfad.newprogectforlesssix.model.Weather
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors

class ReqFromServer(
    private val listener: WeatherLoader,
    private val lat: String,
    private val lon: String
) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun requestOnClick() {
        try {
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}")
            val handler = Handler()

            Thread {
                lateinit var urlConnection: HttpURLConnection
                try {
                    urlConnection = uri.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.addRequestProperty(
                        "X-Yandex-API-Key",
                        "5682b28c-5134-4a21-ad13-9fb93d2399fd"
                    )
                    urlConnection.readTimeout = 10000
                    val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))

                    val weatherDTO: Weather = Gson().fromJson(getLines(reader), Weather::class.java)

                    handler.post {
                        listener.onLoaded(weatherDTO)
                    }
                } catch (e: Exception) {
                    Log.e("", "Error connection", e)
                    e.printStackTrace()
                } finally {
                    urlConnection?.disconnect()
                }
            }.start()
        } catch (e: MalformedURLException) {
            Log.e("", "Error url", e)
            e.printStackTrace()
        }
    }

    interface WeatherLoader {
        fun onLoaded(weatherDTO: Weather)
        fun onFailed(throwable: Throwable)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}