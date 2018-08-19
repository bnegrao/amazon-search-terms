package com.bnegrao.amazonsearchterms;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Profile("test")
@Configuration
public class RestTemplateTestConfiguration {
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
