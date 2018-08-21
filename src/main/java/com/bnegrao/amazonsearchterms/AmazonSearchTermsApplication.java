package com.bnegrao.amazonsearchterms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class AmazonSearchTermsApplication {
	
	int DEFAULT_REQUEST_TIMEOUT_MILISECONDS = 1000;

	public static void main(String[] args) {
		SpringApplication.run(AmazonSearchTermsApplication.class, args);
	}
    
    
    @Bean
	public RestTemplate restTemplate () {
		// Setting Jackson2 as the MessageConverter for all content types. That's
		// necessary because the completion API sends "Content-Type:
		// text/javascript;charset=UTF-8"
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
		messageConverters.add(converter);

		HttpComponentsClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}

    @Bean
	public HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(DEFAULT_REQUEST_TIMEOUT_MILISECONDS);
		return clientHttpRequestFactory;
	}    
}
