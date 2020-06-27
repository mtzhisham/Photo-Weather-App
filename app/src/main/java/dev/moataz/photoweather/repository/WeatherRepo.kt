package dev.moataz.photoweather.repository

import dev.moataz.photoweather.datasource.RemoteDataSource
import dev.moataz.photoweather.model.DataResult
import dev.moataz.photoweather.model.OpenWeatherApiResponse

class WeatherRepo(private val remoteDataSource: RemoteDataSource) : IWeatherRepo {
    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float
    ): DataResult<OpenWeatherApiResponse> {
        val result = remoteDataSource.getCurrentWeather(lat, lon)
        return when (result.cod) {
            200 -> DataResult.Success(result)
            else -> DataResult.Error(Exception(result.message))

        }


    }
}