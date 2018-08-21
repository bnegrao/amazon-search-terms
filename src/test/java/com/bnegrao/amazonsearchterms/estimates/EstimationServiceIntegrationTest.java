package com.bnegrao.amazonsearchterms.estimates;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
		
		EstimationResponse response = estimationService.estimate("gloria bucco", 10000);		
		assertTrue(response.getScore() < 10);
	}	
	
	
	@Test
	public void integrationTestLong() throws InterruptedException, ExecutionException {				
		
		EstimationResponse response = estimationService.estimate("canon", 10000);		
		assertTrue(response.getScore() > 1000);
	}		

}
