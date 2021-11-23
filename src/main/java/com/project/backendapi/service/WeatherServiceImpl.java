package com.project.backendapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendapi.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UriTemplate uriTemplate;

    @Value("${api.openweathermap.key}")
    private String apiKey;

    @Override
    public Weather getCurrentWeather(String city) {
        city = city.replace("_", " ");
        URI uri = uriTemplate.expand(city, apiKey);
        ResponseEntity<String> response = restTemplate
                .getForEntity(uri, String.class);

        return convert(response);
    }

    private Weather convert(ResponseEntity<String> response) {
        try {
            Weather weather = new Weather();
            JsonNode root = objectMapper.readTree(response.getBody());
            weather.setDescription(root.path("weather").get(0).path("main").asText());
            weather.setTemperature(root.path("main").path("temp").asDouble());
            weather.setFeelsLike(root.path("main").path("feels_like").asDouble());
            weather.setWindSpeed(root.path("wind").path("speed").asDouble());
            weather.setLocation(root.path("name").asText());
            return weather;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}
