package com.hfad.newprogectforlesssix.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.hfad.newprogectforlesssix.AppState
import com.hfad.newprogectforlesssix.ReqFromServer
import com.hfad.newprogectforlesssix.viewmodel.MainViewModel
import com.hfad.newprogectforlesssix.databinding.FragmentMainBinding
import com.hfad.newprogectforlesssix.model.Weather
import java.util.*


class MainFragment : Fragment() {
    private var viewModel : MainViewModel = MainViewModel()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private val onLoadListener: ReqFromServer.WeatherLoader = object : ReqFromServer.WeatherLoader{
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
           val loader = ReqFromServer(onLoadListener,latEditText, lonEditText)
            loader.requestOnClick()
        }
    }

    private fun render(data: AppState?) {
        when(data){
            is AppState.Success ->{
                val weatherData = data.weather
                setData(weatherData)
            }
        }

    }

    private fun setData(weatherDataDTO: Weather) {
        binding?.nameCity?.text = weatherDataDTO.info?.url.toString()
        binding?.windSpeed?.text = weatherDataDTO.fact?.wind_speed.toString()
        binding?.temperature?.text= weatherDataDTO.fact?.temp.toString()
        binding?.typeOfWeather?.text = weatherDataDTO.fact?.condition.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
