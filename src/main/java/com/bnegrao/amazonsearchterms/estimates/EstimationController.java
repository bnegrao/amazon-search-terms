package com.bnegrao.amazonsearchterms.estimates;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bnegrao.amazonsearchterms.service.EstimationService;

@RestController
public class EstimationController {
	
	@Autowired
	EstimationService estimationService;
	
	@RequestMapping(path="estimate", method=RequestMethod.GET)
	public EstimationResponse estimate (@RequestParam(value="keyword", required = true) String keyword,
			@RequestParam(value="timeoutMilis", required=false, defaultValue="10000") long timeoutMilis) throws InterruptedException, ExecutionException {			
		
		String keywordClean=keyword.trim().replaceAll("\\s+", " ");
		
		long startTime = new Date().getTime();
		
		int score = estimationService.estimate(keywordClean, timeoutMilis);
		
		long finishTime = new Date().getTime();
		
		return new EstimationResponse(keyword, score , finishTime - startTime);
		
	}
	

}
