package dev.moataz.photoweather.model

import com.squareup.moshi.Json

data class Main(

    @field:Json(name = "temp") val temp: Double,
    @field:Json(name = "feels_like") val feelsLike: Double,
    @field:Json(name = "temp_min") val tempMin: Double,
    @field:Json(name = "temp_max") val tempMax: Double,
    @field:Json(name = "pressure") val pressure: Int,
    @field:Json(name = "humidity") val humidity: Int
)