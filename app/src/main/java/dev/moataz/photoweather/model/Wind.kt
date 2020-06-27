package dev.moataz.photoweather.model

import com.squareup.moshi.Json

data class Wind(

    @field:Json(name = "speed") val speed: Double,
    @field:Json(name = "deg") val deg: Int
)