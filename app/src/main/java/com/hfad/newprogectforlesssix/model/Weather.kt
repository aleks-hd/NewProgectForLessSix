package com.hfad.newprogectforlesssix.model

/*
class Weather( val nameSity: String = "not Name",
               val temperature: Int = 0,
                val windSpeed: Double = 0.0,
               val withOfWeather: String = "not Found") {
}
*/

data class Weather(val fact: Fact?, val info: Info?)
data class Fact(
    val temp: Int?,
    val wind_speed: Double?,
    val condition: String?
)

data class Info(val url: String?)