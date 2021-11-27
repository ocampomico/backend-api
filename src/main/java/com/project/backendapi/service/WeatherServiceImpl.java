package com.project.backendapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendapi.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        try {
            city = city.replace("_", " ");
            URI uri = uriTemplate.expand(city, apiKey);
            JsonNode root = objectMapper
                    .readTree(restTemplate.getForEntity(uri, String.class).getBody());

            Weather weather = new Weather();
            weather.setLocation(root.path("name") == null ? "" : root.path("name").asText());
            weather.setTemperature(root.path("main").path("temp") == null ? 0.0 : root.path("main").path("temp").asDouble());
            weather.setFeelsLike(root.path("main").path("feels_like") == null? 0.0 : root.path("main").path("feels_like").asDouble());
            weather.setWindSpeed(root.path("wind").path("speed") == null ? 0.0 : root.path("wind").path("speed").asDouble());
            weather.setDescription(root.path("weather").get(0).path("main") == null ? "" : root.path("weather").get(0).path("main").asText());
            return weather;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

}
