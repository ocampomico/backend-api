package com.project.backendapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@SpringBootApplication
public class BackendApiApplication {

	@Value("${api.openweathermap.url:null}")
	private String weatherUrl;

	public static void main(String[] args) {
		SpringApplication.run(BackendApiApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public UriTemplate uriTemplate() {
		return new UriTemplate(weatherUrl);
	}

}
