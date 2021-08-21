package com.hfad.newprogectforlesssix.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.hfad.newprogectforlesssix.AppState
import com.hfad.newprogectforlesssix.MainService
import com.hfad.newprogectforlesssix.R
import com.hfad.newprogectforlesssix.ReqFromServer
import com.hfad.newprogectforlesssix.viewmodel.MainViewModel
import com.hfad.newprogectforlesssix.databinding.FragmentMainBinding
import com.hfad.newprogectforlesssix.model.Weather
import java.util.*


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

                    // putExtra("WARNING", onLoadListener )
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
        val temp = intent?.getStringExtra("TEMP")
        Log.i("TEMP","Температура: ${temp}")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
