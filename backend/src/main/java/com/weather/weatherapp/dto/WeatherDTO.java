package com.weather.weatherapp.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record WeatherDTO(
        LocalDate date,
        Integer weatherCode,
        BigDecimal minTemperature,
        BigDecimal maxTemperature,
        BigDecimal estimatedGeneratedEnergy
) {
}
