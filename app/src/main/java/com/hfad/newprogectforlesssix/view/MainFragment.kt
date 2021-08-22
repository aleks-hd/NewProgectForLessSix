package com.hfad.newprogectforlesssix.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hfad.newprogectforlesssix.AppState
import com.hfad.newprogectforlesssix.MainService
import com.hfad.newprogectforlesssix.ReqFromServer
import com.hfad.newprogectforlesssix.viewmodel.MainViewModel
import com.hfad.newprogectforlesssix.databinding.FragmentMainBinding
import com.hfad.newprogectforlesssix.model.Fact
import com.hfad.newprogectforlesssix.model.Info
import com.hfad.newprogectforlesssix.model.Weather

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
        //viewModel.getWeatherFromServer()

        binding?.postFromServer?.setOnClickListener {
            var latEditText: String = binding?.latEdit?.text.toString()
            var lonEditText: String = binding?.lonEdit?.text.toString()
            val loader = ReqFromServer(onLoadListener, latEditText, lonEditText)
            loader.requestOnClick()
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
    }

    private fun render(data: AppState?) {
        when (data) {
            is AppState.Success -> {
                val weatherData = data.weather
                setData(weatherData)
            }
        }

    }

    //Результат работы из класса ReqFromServer
    private fun setData(weatherDataDTO: Weather) {
        binding?.nameCity?.text = weatherDataDTO.info?.url.toString()
        binding?.windSpeed?.text = weatherDataDTO.fact?.wind_speed.toString()
        binding?.temperature?.text = weatherDataDTO.fact?.temp.toString()
        binding?.typeOfWeather?.text = weatherDataDTO.fact?.condition.toString()
    }

    //Результат работы Сервиса
    private fun loadDataFromServerService(content: Context?, intent: Intent?) {
        val temp = intent?.getIntExtra("TEMP", 0)
        val windSpeed = intent?.getDoubleExtra("WIND_SPEED", 0.0)
        val condition = intent?.getStringExtra("CONDITION")
        val url = intent?.getStringExtra("URL")
        val weatherNewFromService = Weather(Fact(temp, windSpeed, condition), Info(url))
        if (intent?.getIntExtra("SUCCESS", 0) == 1) {
            Toast.makeText(
                context,
                "Загрузка данных с помощью сервиса\n произошла успешно",
                Toast.LENGTH_LONG
            ).show()
        }
        setData(weatherNewFromService)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
