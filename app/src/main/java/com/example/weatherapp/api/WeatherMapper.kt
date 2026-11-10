package com.example.weatherapp.api

import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.Weather

fun APICurrentWeather.toWeather() : Weather {
    return Weather (
        date = current?.last_updated ?: "...",
        desc = current?.condition?.text ?: "...",
        temp = current?.temp_c ?: -1.0,
        imgUrl = "https:" + current?.condition?.icon
    )
}

fun APIWeatherForecast.toForecast(): List<Forecast>? {
    return forecast?.forecastday?.map {
        Forecast(
            date = it.date?:"00-00-0000",
            weather = it.day?.condition?.text?:"Erro carregando!",
            tempMin = it.day?.mintemp_c?:-1.0,
            tempMax = it.day?.maxtemp_c?:-1.0,
            imgUrl = ("https:" + it.day?.condition?.icon)
        )
    }
}