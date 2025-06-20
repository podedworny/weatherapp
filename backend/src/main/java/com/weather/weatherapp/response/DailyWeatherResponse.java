package com.weather.weatherapp.response;

import java.math.BigDecimal;
import java.util.List;

public record DailyWeatherResponse(
        List<String> time,
        List<Integer> weather_code,
        List<BigDecimal> apparent_temperature_max,
        List<BigDecimal> apparent_temperature_min,
        List<BigDecimal> sunshine_duration
) {}