package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.DailyWeatherResponse;
import com.weather.weatherapp.dto.SummaryDTO;
import com.weather.weatherapp.dto.WeatherApiResponse;
import com.weather.weatherapp.dto.WeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${WEATHER_API_URL}")
    private String weatherUrl;
    @Value("${PHOTOVOLTAIC_STRENGTH}")
    private BigDecimal photovoltaicsStrength;
    @Value("${PANELS_EFFECTIVITY}")
    private BigDecimal panelsEfficiency;
    private final WebClient webClient = WebClient.create();

    public List<WeatherDTO> getWeeklyWeather(BigDecimal longitude, BigDecimal latitude) {
        DefaultUriBuilderFactory  factory = new DefaultUriBuilderFactory(weatherUrl);

        URI uri = factory.builder()
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("daily", "weather_code,apparent_temperature_max,apparent_temperature_min,sunshine_duration")
                .queryParam("temperature_unit", "celsius")
                .queryParam("timezone", "auto")
                .build();

        System.out.println(uri);

        WeatherApiResponse response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .block();

        if (response == null) return List.of();

        List<WeatherDTO> result = new ArrayList<>();

        for(int i = 0; i < response.daily().time().size(); i++){
            result.add(getWeatherDTO(response.daily(), i));
        }

        return result;
    }

    private WeatherDTO getWeatherDTO(DailyWeatherResponse daily, int i) {
        return WeatherDTO.builder()
                .WeatherCode(daily.weather_code().get(i))
                .Date(LocalDate.parse(daily.time().get(i)))
                .MaxTemperature(daily.apparent_temperature_max().get(i))
                .MinTemperature(daily.apparent_temperature_min().get(i))
                .EstimatedGeneratedEnergy(calculateEstimatedUsage(daily.sunshine_duration().get(i)))
                .build();
    }

    private BigDecimal calculateEstimatedUsage(BigDecimal seconds){
        BigDecimal hours = seconds.divide(BigDecimal.valueOf(3600),2, RoundingMode.HALF_UP);
        return photovoltaicsStrength.multiply(hours).multiply(panelsEfficiency);
    }

    public SummaryDTO getWeeklySummary(BigDecimal altitude, BigDecimal latitude) {
        return null;
    }
}
