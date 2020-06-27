package dev.moataz.photoweather.datasource

import dev.moataz.photoweather.network.ApiService

class RemoteDataSource(private val api: ApiService) {

    suspend fun getCurrentWeather(
        lat: Float,
        lon: Float
    ) = api.getCurrentWeather(lat, lon)


}