package com.weather.weatherapp.response;

public record WeatherApiResponse(
        HourlyWeatherResponse hourly,
        DailyWeatherResponse daily
) {
}
