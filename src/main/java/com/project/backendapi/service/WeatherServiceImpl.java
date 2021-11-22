package com.project.backendapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendapi.model.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${api.openweathermap.key}")
    private String apiKey;

    @Value("${api.openweathermap.url}")
    private String weatherUrl;

    @Override
    public Weather getCurrentWeather() {
        String city = "Los Angeles";
        String country = "USA";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .getForEntity(new UriTemplate(weatherUrl).expand(city, country, apiKey), String.class);
        return convert(response);
    }

    private Weather convert(ResponseEntity<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Weather weather = new Weather();
            JsonNode root = objectMapper.readTree(response.getBody());
            weather.setDescription(root.path("weather").get(0).path("main").asText());
            weather.setTemperature(root.path("main").path("temp").asDouble());
            weather.setFeelsLike(root.path("main").path("feels_like").asDouble());
            weather.setWindSpeed(root.path("wind").path("speed").asDouble());

            return weather;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}
