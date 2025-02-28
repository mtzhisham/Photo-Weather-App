package dev.moataz.photoweather.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.moataz.photoweather.model.DataResult
import dev.moataz.photoweather.model.OpenWeatherApiResponse
import dev.moataz.photoweather.repository.IWeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherSharedViewModel(private val iWeatherRepo: IWeatherRepo) : ViewModel() {


    var error = MutableLiveData<String>()
    var mutableWeatherApiResponse = MutableLiveData<OpenWeatherApiResponse>()
    var mutableCurrentLocation = MutableLiveData<Location>()
    var isLoading = MutableLiveData<Boolean>()


    fun getCurrentWeather(
        lat: Float,
        lon: Float
    ): LiveData<OpenWeatherApiResponse> {

            viewModelScope.launch {
                when (val result =
                    withContext(Dispatchers.IO) { iWeatherRepo.getCurrentWeather(lat, lon) }) {

                    is DataResult.Success -> {
                        mutableWeatherApiResponse.value = result.content
                        error.value = null
                        isLoading.value = true
                        isLoading.value = false
                    }
                    is DataResult.Error -> {
                        mutableWeatherApiResponse.value = null
                        error.value = result.exception.message
                        isLoading.value = false
                    }
                }
            }
        return mutableWeatherApiResponse
    }


}



