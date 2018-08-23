package com.bnegrao.amazonsearchterms.estimates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bnegrao.amazonsearchterms.service.EstimationService;

/**
 * This test will call the real amazon API on the internet
 * @author bnegrao
 *
 */

public class EstimationServiceUnitTests {
	
	
	@Test
	public void calculateScoreTest() {
		assertEquals (100, EstimationService.calculateScore(100, 100));
		assertEquals (99, EstimationService.calculateScore(1999, 2000));
		assertEquals (1, EstimationService.calculateScore(1, 100));
		assertEquals (1, EstimationService.calculateScore(1, 2000));
		assertEquals (50, EstimationService.calculateScore(1000, 2000));
		assertEquals (25, EstimationService.calculateScore(500, 2000));
		assertEquals (75, EstimationService.calculateScore(1500, 2000));
		assertEquals (80, EstimationService.calculateScore(1600, 2000));
		assertEquals (0, EstimationService.calculateScore(0, 2000));		
	}
	
}
