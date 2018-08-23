package com.bnegrao.amazonsearchterms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class AmazonAPIService {

	int QUEUE_CAPACITY = 10000;

	@Value("${completion.api.url}")
	private String COMPLETION_SERVICE_URL;

	private static final Logger log = LoggerFactory.getLogger(AmazonAPIService.class);

	private RestTemplate restTemplate;

	private Integer nRequests;
	
	private Integer nRequestsNoResults;

	private HttpComponentsClientHttpRequestFactory requestsFactory;

	@Autowired
	public AmazonAPIService(RestTemplate restTemplate, HttpComponentsClientHttpRequestFactory requestsFactory) {
		this.restTemplate = restTemplate;
		this.requestsFactory = requestsFactory;
	}

	// all keywords that were retrieved from the completion api
	Set<String> allKeywordsFound = Collections.synchronizedSet(new TreeSet<String>());

	// keywords retrieved from the completion api, that will be used as search terms
	// for subsequent recursive searches.
	LinkedBlockingDeque<String> toBeSearched = new LinkedBlockingDeque<>(QUEUE_CAPACITY);

	public AmazonApiSearchResult recursiveSearch(String keyword, long timeout) throws InterruptedException, ExecutionException {
		long startTime = new Date().getTime();
		long timeToStop = startTime + timeout;
		
		allKeywordsFound.clear();
		toBeSearched.clear();

		toBeSearched.add(keyword);
		nRequests = 0;
		nRequestsNoResults = 0;

		String term = null;
		while ((term = toBeSearched.pollFirst(300, TimeUnit.MILLISECONDS)) != null) {
			if (new Date().getTime() > timeToStop) {
				break;
			}

			// get the topTen results in a synchronous request
			List<String> topResults = callAmazonApi(term, timeToStop);
			if (topResults == null ) {
				continue;
			}
			
			int topResultsSize = topResults.size();
		
			boolean termRemovedFromResults = topResults.remove(term);
			topResults.stream().forEach(s -> {
				if (!allKeywordsFound.contains(s))
					toBeSearched.add(s);
			});
			allKeywordsFound.addAll(topResults);
			if (termRemovedFromResults) allKeywordsFound.add(term);
			
			
			// if there is less than 10 results there is no need to probe for other values.			
			if (topResultsSize < 10) {
				continue;
			}


			// probe for other results by concatenating a character at the end of the
			// keyword 
			int maxNumberOfWords = 4;
			if (term.split("\\s+").length < maxNumberOfWords) {
				if (new Date().getTime() > timeToStop) {
					break;
				}
				for (char c : "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray()) {
					if (term.matches(".*\\d+.*") && (c+"").matches("\\d") ) {
						// don't probe for other numerals if the keyword already has numerals
						continue;
					}														
					
					String probeTerm = term + " " + c;
					CompletableFuture<List<String>> probeNewTerms = CompletableFuture.supplyAsync(() -> {
						return callAmazonApi(probeTerm, timeToStop);
					});

					probeNewTerms.thenApply(searchResults -> {
						if (searchResults == null) {
							return null;
						} 
						boolean probeTermRemovedFromResults = searchResults.remove(probeTerm);
						searchResults.stream().forEach(s -> {
							if (!allKeywordsFound.contains(s))
								toBeSearched.add(s);
						});
						allKeywordsFound.addAll(searchResults);
						if (probeTermRemovedFromResults) allKeywordsFound.add(probeTerm);	
												
						return null;

					});
				}
			}
		}
		if (toBeSearched != null && !toBeSearched.isEmpty()) {
			log.info("Search timedout before I could examine " + toBeSearched.size() + " terms.");
			
			allKeywordsFound.addAll(toBeSearched);
		}


		log.info("Invoked " + nRequests + " to the amazon completion API and found " + allKeywordsFound.size()
				+ " unique results.");
		long finishTime = new Date().getTime();	
		long timeElapsedMilis = finishTime - startTime;
		boolean searchInterruptedDueToTimeout = toBeSearched != null && toBeSearched.size() > 0;
		int keywordsNotSearchedDueToTimeout = searchInterruptedDueToTimeout ? toBeSearched.size() : 0;
		return new AmazonApiSearchResult(allKeywordsFound , nRequests, nRequestsNoResults, searchInterruptedDueToTimeout, keywordsNotSearchedDueToTimeout, timeElapsedMilis);
	}


	@SuppressWarnings("unchecked")
	public List<String> callAmazonApi(String prefix, long timeToStop) {		
		long nowTime = new Date().getTime();
		if (nowTime > timeToStop) {
			return null;
		}

		ArrayList<Object> json = null;

		try {
			log.info("Search: [" + prefix + "]");
			requestsFactory.setReadTimeout((int) (timeToStop - nowTime));
			json = restTemplate.getForObject(COMPLETION_SERVICE_URL + prefix, ArrayList.class);
			// log.info(json.toString());
			nRequests++;
		} catch (ResourceAccessException e) {
			log.error("request for prefix '" + prefix + "' aborted due to timeout.");
			return null;
		}

		List<String> searchResults = null;

		if (json != null) {
			ArrayList<Object> response = (ArrayList<Object>) json;
			if (response.size() >= 2) {
				if (response.get(1) instanceof ArrayList) {
					searchResults = (ArrayList<String>) response.get(1);
				}
			}
		}
		
		if (json == null || searchResults == null || searchResults.size() == 0) {
			nRequestsNoResults++;
		}

		return searchResults;
	}

}
