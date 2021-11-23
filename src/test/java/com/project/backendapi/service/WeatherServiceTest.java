package com.project.backendapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendapi.model.Weather;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceTest {

    @InjectMocks
    @Spy
    WeatherServiceImpl weatherService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    UriTemplate uriTemplate;

    @Mock
    JsonNode jsonNode;

    Weather weather;

    @Before
    public void init() {
//        uriTemplate = new UriTemplate("http://localhost:8080");

        MockitoAnnotations.initMocks(this);

        weather = Weather.builder()
                .location("Los Angeles")
                .temperature(71.2)
                .build();
    }

    @Test
    public void getWeatherByCity() throws JsonProcessingException {
        // Given a weather for a city
        // When getCurrentWeather is called
        // Then it should return the weather
        // For that specific city

//        UriTemplate uriTemplate = new UriTemplate("http://localhost:8080/v1/weather/Los Angeles");
        URI uri = URI.create("test");

        ResponseEntity<String> test = new ResponseEntity<>(weather.toString(), HttpStatus.OK);
//        when(uriTemplate.expand("LOS ANGELES", "test")).thenReturn(uri);
//
//        doReturn(uri).when(uriTemplate).expand(Mockito.anyString(), Mockito.anyString());
//        doReturn(test).when(restTemplate).getForEntity(Mockito.anyString(), eq(String.class));
//        doReturn(jsonNode).when(objectMapper).readTree(anyString());
//
        when(uriTemplate.expand(anyString(), anyString())).thenReturn(uri);
        when(restTemplate.getForEntity(uri, String.class)).thenReturn(test);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);



        Weather testWeather = weatherService.getCurrentWeather("Los Angeles");
        assertEquals(weather.getTemperature(), testWeather.getTemperature());
    }
}
