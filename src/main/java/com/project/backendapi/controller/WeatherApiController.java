package com.project.backendapi.controller;

import com.project.backendapi.model.City;
import com.project.backendapi.model.Weather;
import com.project.backendapi.service.WeatherService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${openapi.project.base-path:/v1}")
public class WeatherApiController implements WeatherApi {

    @Autowired
    private WeatherService weatherService;

    @Override
    public ResponseEntity<Weather> weatherCityGet(@ApiParam(value = "", required = true, allowableValues = "LOS_ANGELES, SAN_DIEGO, HUNTINGTON_BEACH, SAN_FRANCISCO") @PathVariable("city") City city) {
        return ResponseEntity.ok(weatherService.getCurrentWeather(city.getValue()));
    }
}
