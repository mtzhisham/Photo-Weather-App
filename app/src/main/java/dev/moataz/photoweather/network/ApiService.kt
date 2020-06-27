package dev.moataz.photoweather.network

import dev.moataz.photoweather.model.OpenWeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): OpenWeatherApiResponse

}

