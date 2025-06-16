package com.weather.weatherapp.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record WeatherDTO(
        LocalDate Date,
        Integer WeatherCode,
        BigDecimal MinTemperature,
        BigDecimal MaxTemperature,
        BigDecimal EstimatedGeneratedEnergy
) {
}
