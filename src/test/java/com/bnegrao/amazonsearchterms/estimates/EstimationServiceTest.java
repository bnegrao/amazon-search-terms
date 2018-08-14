package com.bnegrao.amazonsearchterms.estimates;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bnegrao.amazonsearchterms.AmazonSearchTermsApplication;
import com.bnegrao.amazonsearchterms.service.AmazonAPIServiceSyncImpl;
import com.bnegrao.amazonsearchterms.service.EstimationService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AmazonSearchTermsApplication.class, EstimationService.class, AmazonAPIServiceSyncImpl.class})
public class EstimationServiceTest {
	
	@Autowired
	EstimationService estimationService;

	@Test
	public void estimateTest() throws InterruptedException, ExecutionException {
		int score = estimationService.estimate("harry potter wand", 10000);		
		assertEquals(100, score);
	}

}
