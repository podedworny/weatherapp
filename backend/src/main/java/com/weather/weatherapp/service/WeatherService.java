package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.SummaryDTO;
import com.weather.weatherapp.dto.WeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${WEATHER_API_URL}")
    private String weatherUrl;

    public List<WeatherDTO> getWeeklyWeather(BigDecimal altitude, BigDecimal latitude) {
        return null;
    }

    public SummaryDTO getWeeklySummary(BigDecimal altitude, BigDecimal latitude) {
        return null;
    }
}
