package com.hfad.newprogectforlesssix

import android.app.IntentService

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.hfad.newprogectforlesssix.model.Weather
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors

private const val TAG = "MainServiceTAG"

class MainService(name: String = "MainService") : IntentService(name) {

    private val broadcastIntent = Intent("LOAD_DATA_SERVER")

    override fun onHandleIntent(intent: Intent?) {
        intent?.getStringExtra("lat")?.let { createLogMessage(it) }
        intent?.getStringExtra("lon")?.let { createLogMessage(it) }
       val lat = intent?.getStringExtra("lat")
        val lon = intent?.getStringExtra("lon")
        requestFromServer(lat,lon)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestFromServer(lat: String?, lon: String?) {
       lateinit var urlConnection: HttpURLConnection
        try {
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}")
            try {
                urlConnection = uri.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.addRequestProperty(
                    "X-Yandex-API-Key",
                    "5682b28c-5134-4a21-ad13-9fb93d2399fd"
                )
                urlConnection.readTimeout = 10000
                val bufferRead = BufferedReader(InputStreamReader(urlConnection.inputStream))

                val weatherFS: Weather = Gson().fromJson(getLines(bufferRead), Weather::class.java)
                onResponse(weatherFS)
               } catch (e: Exception) {
                Log.e("", "Error connection", e)
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }

        } catch (e: MalformedURLException) {
            Log.e("", "Error url", e)
            e.printStackTrace()
        }
    }

    private fun onResponse(weatherFS: Weather) {
        val fact = weatherFS.fact
        val info = weatherFS.info
        if (fact==null && info ==null){
            onEmtyResponese()
        } else{
            onSuccessResponce(fact?.temp, fact?.wind_speed,fact?.condition,info?.url)
        }
    }

    private fun onEmtyResponese() {
        TODO("Not yet implemented")
    }

    private fun onSuccessResponce(temp: Int?, windSpeed: Double?, condition: String?, url: String?) {

        broadcastIntent.putExtra("TEMP",temp)
        broadcastIntent.putExtra("WIND_SPEED", windSpeed)
        broadcastIntent.putExtra("CONDITION", condition)
        broadcastIntent.putExtra("URL",url)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

    }



    private fun getLines(bufferRead: BufferedReader): String {
            return bufferRead.lines().collect(Collectors.joining("\n"))
        }

    override fun onCreate() {
        super.onCreate()
        createLogMessage("onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        createLogMessage("onStart")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createLogMessage("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        createLogMessage("onDestroy")
    }

    private fun createLogMessage(message: String) {
        Log.d(TAG, message)
    }



}