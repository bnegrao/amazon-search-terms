package com.bnegrao.amazonsearchterms;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.bnegrao.amazonsearchterms.service.AmazonAPIService;
import com.bnegrao.amazonsearchterms.service.AmazonAPIServiceAsyncImpl;
import com.bnegrao.amazonsearchterms.service.EstimationService;

@SpringBootApplication
@EnableAsync
public class AmazonSearchTermsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonSearchTermsApplication.class, args);
	}
	
//    @Bean
//    public Executor asyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(500);
//        executor.setThreadNamePrefix("CompletionAPIService-");
//        executor.initialize();
//        return executor;
//    }	
    
    @Bean
    public AmazonAPIService amazonAPIService() {
    	return new AmazonAPIServiceAsyncImpl();
    }
    
    @Bean
    public EstimationService estimationService(AmazonAPIService amazonAutocompleteService) {
    	return new EstimationService(amazonAutocompleteService);
    }
}
