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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
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
        weather = Weather.builder().temperature(0.0).build();
    }

    @Test
    public void getWeatherByCity() throws JsonProcessingException {
        // Given a weather for a city
        // When getCurrentWeather is called
        // Then it should return the weather
        // For that specific city

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        URI uri = uriTemplate.expand(eq("test"), any());
        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                "some body test",
                header,
                HttpStatus.OK
        );

        JsonNode locationNode, tempNode, feelNode, windNode, descriptionNode;
        locationNode = tempNode = feelNode = windNode = descriptionNode = mock(JsonNode.class);
        when(restTemplate.getForEntity(uri, String.class)).thenReturn(responseEntity);
        when(objectMapper.readTree(responseEntity.getBody())).thenReturn(jsonNode);


        when(jsonNode.path(any())).thenReturn(locationNode);
        when(jsonNode.path(any()).path(any())).thenReturn(tempNode);
        when(jsonNode.path(any()).path(any())).thenReturn(feelNode);
        when(jsonNode.path(any()).path(any())).thenReturn(windNode);
        when(jsonNode.path(any()).get(any())).thenReturn(descriptionNode);

        descriptionNode = jsonNode.path(any()).get(any());

        when(descriptionNode.path(any()).path(any())).thenReturn(mock(JsonNode.class));

        Weather testWeather = weatherService.getCurrentWeather("LOS_ANGELES");
        assertEquals(weather.getTemperature(), testWeather.getTemperature());
    }
}
