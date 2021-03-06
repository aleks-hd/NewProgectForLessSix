package com.hfad.newprogectforlesssix.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.hfad.newprogectforlesssix.AppState
import com.hfad.newprogectforlesssix.MainService
import com.hfad.newprogectforlesssix.ReqFromServer
import com.hfad.newprogectforlesssix.databinding.FragmentMainBinding
import com.hfad.newprogectforlesssix.model.Fact
import com.hfad.newprogectforlesssix.model.Info
import com.hfad.newprogectforlesssix.model.Weather
import com.hfad.newprogectforlesssix.viewmodel.MainViewModel
import okhttp3.*
import java.io.IOException
import  com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou


class MainFragment : Fragment() {
    private var viewModel: MainViewModel = MainViewModel()
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding
    private val loadresultReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(content: Context?, intent: Intent?) {
            loadDataFromServerService(content, intent)
        }
    }


    private val onLoadListener: ReqFromServer.WeatherLoader = object : ReqFromServer.WeatherLoader {
        override fun onLoaded(weatherDTO: Weather) {
            setData(weatherDTO)
        }

        override fun onFailed(throwable: Throwable) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                loadresultReceiver,
                IntentFilter("LOAD_DATA_SERVER")
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData().observe(viewLifecycleOwner, Observer { render(it) })

        binding?.postFromServer?.setOnClickListener {
            var latEditText: Double = binding?.latEdit?.text.toString().toDouble()
            var lonEditText: Double = binding?.lonEdit?.text.toString().toDouble()
            viewModel.getWeatherFromServer(latEditText,lonEditText)


            /* var latEditText: String = binding?.latEdit?.text.toString()
                var lonEditText: String = binding?.lonEdit?.text.toString()
                val loader = ReqFromServer(onLoadListener, latEditText, lonEditText)
                loader.requestOnClick()*/
        }

        binding?.postFromServerService?.setOnClickListener {
            context?.let {
                it.startService(Intent(it, MainService::class.java).apply {
                    var latEditText: String = binding?.latEdit?.text.toString()
                    var lonEditText: String = binding?.lonEdit?.text.toString()
                    putExtra("lat", latEditText)
                    putExtra("lon", lonEditText)
                })
            }
        }
        binding?.postFromServerokHttp?.setOnClickListener {
            var latEditText: String = binding?.latEdit?.text.toString()
            var lonEditText: String = binding?.lonEdit?.text.toString()
            getWeatherOkHttp(latEditText, lonEditText)
        }
    }


    private fun render(data: AppState?) {
        when (data) {
            is AppState.Success -> {
                val weatherData = data.weather
                setData(weatherData)
            }
        }

    }

    //?????????????????? ???????????? ???? ???????????? ReqFromServer
    private fun setData(weatherDataDTO: Weather) {
        binding?.nameCity?.text = weatherDataDTO.info?.url.toString()
        binding?.windSpeed?.text = weatherDataDTO.fact?.wind_speed.toString()
        binding?.temperature?.text = weatherDataDTO.fact?.temp.toString()
        binding?.typeOfWeather?.text = weatherDataDTO.fact?.condition.toString()



        val iconName = weatherDataDTO.fact?.icon.toString()
        setIcon(iconName)
    }

    private fun setIcon(iconName: String) {

            GlideToVectorYou.justLoadImage(
                activity,
                Uri.parse("https://yastatic.net/weather/i/icons/blueye/color/svg/${iconName}.svg"),
                binding?.iconImage
                )

        }


    //?????????????????? ???????????? ??????????????
    private fun loadDataFromServerService(content: Context?, intent: Intent?) {
        val temp = intent?.getIntExtra("TEMP", 0)
        val windSpeed = intent?.getDoubleExtra("WIND_SPEED", 0.0)
        val condition = intent?.getStringExtra("CONDITION")
        val url = intent?.getStringExtra("URL")
        val icon = intent?.getStringExtra("ICON")
        val weatherNewFromService = Weather(Fact(temp, windSpeed, condition,icon), Info(url))
        if (intent?.getIntExtra("SUCCESS", 0) == 1) {
            Toast.makeText(
                context,
                "???????????????? ???????????? ?? ?????????????? ??????????????\n ?????????????????? ??????????????",
                Toast.LENGTH_LONG
            ).show()
        }
        setData(weatherNewFromService)
    }

    private fun getWeatherOkHttp(latEditText: String, lonEditText: String) {

        val client = OkHttpClient()
        val builder: Request.Builder = Request.Builder()
        builder.header("X-Yandex-API-Key", "5682b28c-5134-4a21-ad13-9fb93d2399fd")
        builder.url("https://api.weather.yandex.ru/v2/informers?lat=${latEditText}&lon=${lonEditText}")
        val request: Request = builder.build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            val handler: Handler = Handler()

            @Throws(IOException::class)
            override fun onFailure(call: Call, e: IOException) {
                TODO("Process error")
            }

            override fun onResponse(call: Call, response: Response) {
                val serverResponse: String? = response.body()?.string()
                if (response.isSuccessful && serverResponse != null) {
                    handler.post {
                        setData(Gson().fromJson(serverResponse, Weather::class.java))
                    }
                } else {
                    TODO("Process error")
                }

            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
