package com.bnegrao.amazonsearchterms.service;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bnegrao.amazonsearchterms.estimates.EstimationResponse;

@Service
public class EstimationService {

	private static final Logger logger = LoggerFactory.getLogger(EstimationService.class);

	private AmazonAPIService amazonService;
	
	@Value("${estimation.number.of.keywords.upper.limit}")
	private int nKeywordsUpperLimit;

	@Autowired
	public EstimationService(AmazonAPIService amazonAPIService) {
		this.amazonService = amazonAPIService;
	}

	/* (non-Javadoc)
	 * @see com.bnegrao.amazonsearchterms.estimates.EstimationService#estimate(java.lang.String)
	 */
	public EstimationResponse estimate(String keyword, long timeoutMiliseconds) throws InterruptedException, ExecutionException {		
		
		AmazonApiSearchResult amazonApiSearchResult = amazonService.recursiveSearch(keyword, timeoutMiliseconds);			

		int score = calculateScore(amazonApiSearchResult.getKeywordsList().size(), nKeywordsUpperLimit);		
		
		logger.info("Keyword '" + keyword + "' had score " + score);

		return new EstimationResponse(keyword, score);		
			
	}

	public static int calculateScore(int nKeywordsFound, int upperLimit) {
		
		if (nKeywordsFound >= upperLimit) {
			return 100;
		}
		
		int score = ( nKeywordsFound * 100 ) / upperLimit;
		
		// adjust the score for the keywords that had very few results (like 'gloria bucco')
		if (nKeywordsFound > 0 && score == 0) {
			score = 1;
		}		
		
		return score;
	}

}