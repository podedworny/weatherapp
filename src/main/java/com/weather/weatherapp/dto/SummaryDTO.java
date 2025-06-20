package com.weather.weatherapp.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SummaryDTO(
        BigDecimal averagePressure,
        BigDecimal averageSunExposure,
        BigDecimal minTempWeek,
        BigDecimal maxTempWeek,
        String description
) {
}
