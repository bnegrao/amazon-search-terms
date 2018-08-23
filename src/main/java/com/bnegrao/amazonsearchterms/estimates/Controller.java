package com.bnegrao.amazonsearchterms.estimates;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bnegrao.amazonsearchterms.service.AmazonAPIService;
import com.bnegrao.amazonsearchterms.service.AmazonApiSearchResult;
import com.bnegrao.amazonsearchterms.service.EstimationService;

@RestController
public class Controller {
	
	@Autowired
	private EstimationService estimationService;
	
	@Autowired
	private AmazonAPIService amazonApiService;
	
	@Value("${estimation.timeoutMilis}")
	private long estimationTimeoutMilis;
	
	@RequestMapping(path="estimate", method=RequestMethod.GET)
	public EstimationResponse estimate (@RequestParam(value="keyword") String keyword) throws InterruptedException, ExecutionException {			
		
		String keywordClean=keyword.trim().replaceAll("\\s+", " ");
				
		EstimationResponse response = estimationService.estimate(keywordClean, estimationTimeoutMilis);
		
		return response;		
		
	}
	
	@RequestMapping(path="search", method=RequestMethod.GET)
	public AmazonApiSearchResult search (@RequestParam(value="keyword", required = true) String keyword,
			@RequestParam(value="timeoutMilis", required=false, defaultValue="10000") long timeoutMilis) throws InterruptedException, ExecutionException {			
		
		String keywordClean=keyword.trim().replaceAll("\\s+", " ");					
		
		return amazonApiService.recursiveSearch(keywordClean, timeoutMilis);
				
	}	
	

}
