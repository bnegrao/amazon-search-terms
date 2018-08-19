package com.bnegrao.amazonsearchterms.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public int estimate(String keyword, int timeoutMiliseconds) throws InterruptedException, ExecutionException {
		Set<String> autocompleteList = amazonService.recursiveSearch(keyword, new Date().getTime() + timeoutMiliseconds);


		int score = autocompleteList.size();
		logger.info("Keyword '" + keyword + "' had score " + score);

		return score;
	}

}