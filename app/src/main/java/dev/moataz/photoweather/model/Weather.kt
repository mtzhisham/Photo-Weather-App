package dev.moataz.photoweather.model

import com.squareup.moshi.Json

data class Weather (

    @field:Json(name ="id") val id : Int,
    @field:Json(name ="main") val main : String,
    @field:Json(name ="description") val description : String,
    @field:Json(name ="icon") val icon : String
)