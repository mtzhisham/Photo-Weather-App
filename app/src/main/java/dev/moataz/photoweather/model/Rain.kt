package dev.moataz.photoweather.model

import com.squareup.moshi.Json

data class Rain(

    @field:Json(name = "1h") val hOne: Double
)