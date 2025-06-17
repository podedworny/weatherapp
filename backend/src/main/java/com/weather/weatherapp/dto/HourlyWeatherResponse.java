package com.weather.weatherapp.dto;

import java.math.BigDecimal;
import java.util.List;

public record HourlyWeatherResponse(
        List<BigDecimal> pressure_msl
) {
}
