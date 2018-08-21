package com.bnegrao.amazonsearchterms.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnegrao.amazonsearchterms.estimates.EstimationResponse;

@Service
public class EstimationService {

	private static final Logger logger = LoggerFactory.getLogger(EstimationService.class);

	private AmazonAPIService amazonService;

	@Autowired
	public EstimationService(AmazonAPIService amazonAPIService) {
		this.amazonService = amazonAPIService;
	}

	/* (non-Javadoc)
	 * @see com.bnegrao.amazonsearchterms.estimates.EstimationService#estimate(java.lang.String)
	 */
	public EstimationResponse estimate(String keyword, long timeoutMiliseconds) throws InterruptedException, ExecutionException {
		long startTime = new Date().getTime();
		
		Set<String> autocompleteList = amazonService.recursiveSearch(keyword, timeoutMiliseconds);
		
		long finishTime = new Date().getTime();	

		int score = autocompleteList.size();
		logger.info("Keyword '" + keyword + "' had score " + score);

		return new EstimationResponse(keyword, score, finishTime - startTime, autocompleteList);
		
			
	}

}