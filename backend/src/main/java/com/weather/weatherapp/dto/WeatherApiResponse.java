package com.weather.weatherapp.dto;

public record WeatherApiResponse(
        HourlyWeatherResponse hourly,
        DailyWeatherResponse daily
) {
}
