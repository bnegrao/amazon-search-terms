package com.bnegrao.amazonsearchterms.estimates;

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
	public EstimationResponse estimate (@RequestParam(value="keyword", required = true) String keyword) throws InterruptedException, ExecutionException {
		
		return new EstimationResponse(keyword, estimationService.estimate(keyword, 30000));
		
	}
	

}
