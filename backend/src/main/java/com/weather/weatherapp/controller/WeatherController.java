package com.weather.weatherapp.controller;


import com.weather.weatherapp.dto.SummaryDTO;
import com.weather.weatherapp.dto.WeatherDTO;
import com.weather.weatherapp.service.WeatherService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<List<WeatherDTO>> getWeeklyWeather(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude
            ) {
        return ResponseEntity.ok(weatherService.getWeeklyWeather(longitude, latitude));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getWeeklySummary(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude
    ){
        return ResponseEntity.ok(weatherService.getWeeklySummary(longitude, latitude));
    }
}
