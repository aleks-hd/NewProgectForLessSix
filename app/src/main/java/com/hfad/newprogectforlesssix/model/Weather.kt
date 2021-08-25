package com.hfad.newprogectforlesssix.model


data class Weather(val fact: Fact? , val info: Info?)


data class Fact(
    val temp: Int?,
    val wind_speed: Double?,
    val condition: String?,
    val icon:String?
)

data class Info(val url: String?)