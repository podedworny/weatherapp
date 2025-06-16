package com.weather.weatherapp.controller;


import com.weather.weatherapp.dto.SummaryDTO;
import com.weather.weatherapp.dto.WeatherDTO;
import com.weather.weatherapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<List<WeatherDTO>> getWeeklyWeather(
            @RequestParam BigDecimal altitude,
            @RequestParam BigDecimal latitude
            ) {
        return ResponseEntity.ok(weatherService.getWeeklyWeather(altitude, latitude));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getWeeklySummary(
            @RequestParam BigDecimal altitude,
            @RequestParam BigDecimal latitude
    ){
        return ResponseEntity.ok(weatherService.getWeeklySummary(altitude, latitude));
    }
}
