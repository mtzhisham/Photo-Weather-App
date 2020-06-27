package dev.moataz.photoweather.model

import com.squareup.moshi.Json

data class Current(

    @field:Json(name = "dt") val dt: Int,
    @field:Json(name = "sunrise") val sunrise: Int,
    @field:Json(name = "sunset") val sunset: Int,
    @field:Json(name = "temp") val temp: Double,
    @field:Json(name = "feels_like") val feelsLike: Double,
    @field:Json(name = "pressure") val pressure: Int,
    @field:Json(name = "humidity") val humidity: Int,
    @field:Json(name = "dew_point") val dewPoint: Double,
    @field:Json(name = "uvi") val uvi: Double,
    @field:Json(name = "clouds") val clouds: Int,
    @field:Json(name = "visibility") val visibility: Int,
    @field:Json(name = "wind_speed") val windSpeed: Double,
    @field:Json(name = "wind_deg") val windDeg: Int,
    @field:Json(name = "weather") val weather: List<Weather>
)