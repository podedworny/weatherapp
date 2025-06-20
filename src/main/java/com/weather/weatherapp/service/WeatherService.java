package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.*;
import com.weather.weatherapp.response.DailyWeatherResponse;
import com.weather.weatherapp.response.HourlyWeatherResponse;
import com.weather.weatherapp.response.WeatherApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
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
    private final BigDecimal photovoltaicsStrength = BigDecimal.valueOf(2.5);
    private final BigDecimal panelsEfficiency =  BigDecimal.valueOf(0.2);
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

        try {
            WeatherApiResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), ClientResponse::createException)
                    .bodyToMono(WeatherApiResponse.class)
                    .block();

            if (response == null) return List.of();

            List<WeatherDTO> result = new ArrayList<>();

            for (int i = 0; i < response.daily().time().size(); i++) {
                result.add(getWeatherDTO(response.daily(), i));
            }

            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }

    private WeatherDTO getWeatherDTO(DailyWeatherResponse daily, int i) {
        return WeatherDTO.builder()
                .weatherCode(daily.weather_code().get(i))
                .date(LocalDate.parse(daily.time().get(i)))
                .maxTemperature(daily.apparent_temperature_max().get(i))
                .minTemperature(daily.apparent_temperature_min().get(i))
                .estimatedGeneratedEnergy(calculateEstimatedUsage(daily.sunshine_duration().get(i)))
                .build();
    }

    private BigDecimal calculateEstimatedUsage(BigDecimal seconds){
        BigDecimal hours = seconds.divide(BigDecimal.valueOf(3600),2, RoundingMode.HALF_UP);
        return photovoltaicsStrength.multiply(hours).multiply(panelsEfficiency);
    }

    public SummaryDTO getWeeklySummary(BigDecimal longitude, BigDecimal latitude) {
        DefaultUriBuilderFactory  factory = new DefaultUriBuilderFactory(weatherUrl);

        URI uri = factory.builder()
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("hourly", "pressure_msl")
                .queryParam("daily", "weather_code,apparent_temperature_max,apparent_temperature_min,sunshine_duration")
                .queryParam("temperature_unit", "celsius")
                .queryParam("timezone", "auto")
                .build();

        try {
            WeatherApiResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), ClientResponse::createException)
                    .bodyToMono(WeatherApiResponse.class)
                    .block();

            if (response == null) return null;

            return getSummaryDTO(response.hourly(), response.daily());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    private SummaryDTO getSummaryDTO(HourlyWeatherResponse hourly, DailyWeatherResponse daily) {
        BigDecimal sum = hourly.pressure_msl().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(hourly.pressure_msl().size()), 2, RoundingMode.HALF_UP);
        BigDecimal max = daily.apparent_temperature_max().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal min = daily.apparent_temperature_min().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal sunExposureSum = daily.sunshine_duration().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sunExposureAvg = sunExposureSum.divide(BigDecimal.valueOf(daily.sunshine_duration().size()), 2, RoundingMode.HALF_UP);

        List<Integer> weatherCodes = daily.weather_code();
        String message;
        long sunnyDays = weatherCodes.stream().filter(c -> c == 0 || c == 1).count();
        long cloudyDays = weatherCodes.stream().filter(c -> c == 2 || c == 3 || c == 45 || c == 48).count();

        if (sunnyDays >= 4) {
            message = "W tym tygodniu będą przeważać słoneczne dni.";
        } else if (cloudyDays >= 4 || (sunnyDays + cloudyDays) >= 4) {
            message = "Dominować będzie pochmurna pogoda, bez większych opadów.";
        } else {
            message = "Prognoza wskazuje na dni z opadami.";
        }
        return SummaryDTO.builder()
                .averagePressure(average)
                .maxTempWeek(max)
                .minTempWeek(min)
                .description(message)
                .averageSunExposure(sunExposureAvg.divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP))
                .build();
    }
}
