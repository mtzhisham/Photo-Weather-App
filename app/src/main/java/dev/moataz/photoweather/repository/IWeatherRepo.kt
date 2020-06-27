package dev.moataz.photoweather.repository

import dev.moataz.photoweather.model.Coord
import dev.moataz.photoweather.model.DataResult
import dev.moataz.photoweather.model.OpenWeatherApiResponse

interface IWeatherRepo {

    suspend fun getCurrentWeather(lat : Float,
                              lon : Float
    ) : DataResult<OpenWeatherApiResponse>


}