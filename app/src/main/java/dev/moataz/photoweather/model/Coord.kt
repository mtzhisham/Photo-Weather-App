package dev.moataz.photoweather.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Coord (

    @Json(name ="lon")
  val lon : Double,
    @Json(name ="lat")
   val lat : Double
)