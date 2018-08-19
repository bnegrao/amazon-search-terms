package com.bnegrao.amazonsearchterms.estimates;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.bnegrao.amazonsearchterms.AmazonSearchTermsApplication;
import com.bnegrao.amazonsearchterms.service.AmazonAPIService;
import com.bnegrao.amazonsearchterms.service.EstimationService;

/**
 * This test will call the real amazon API on the internet
 * @author bnegrao
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AmazonSearchTermsApplication.class, EstimationService.class, AmazonAPIService.class})
public class EstimationServiceIntegrationTest {
	
	@Autowired
	EstimationService estimationService;
	

	@Test
	public void integrationTest() throws InterruptedException, ExecutionException {				
		
		int score = estimationService.estimate("harry potter", 10000);		
		assertTrue(score > 1000);
	}		
	

}
