package com.bnegrao.amazonsearchterms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	private HttpComponentsClientHttpRequestFactory requestsFactory;

	@Autowired
	public AmazonAPIService(RestTemplate restTemplate, HttpComponentsClientHttpRequestFactory requestsFactory) {
		this.restTemplate = restTemplate;
		this.requestsFactory = requestsFactory;
	}

	// all keywords that were retrieved from the completion api
	Set<String> allKeywordsFound = Collections.synchronizedSet(new HashSet<String>());

	// keywords retrieved from the completion api, that will be used as search terms
	// for subsequent
	// recursive searches.
	LinkedBlockingDeque<String> toBeSearched = new LinkedBlockingDeque<>(QUEUE_CAPACITY);

	public Set<String> recursiveSearch(String keyword, long timeout) throws InterruptedException, ExecutionException {
		long timeToStop = new Date().getTime() + timeout;
		
		allKeywordsFound.clear();
		toBeSearched.clear();

		toBeSearched.add(keyword);
		nRequests = 0;

		String term = null;
		while ((term = toBeSearched.pollFirst(300, TimeUnit.MILLISECONDS)) != null) {
			if (new Date().getTime() > timeToStop) {
				break;
			}

			// get the topTen results in a synchronous request
			List<String> topTenResults = callAmazonApi(term, timeToStop);
			if (topTenResults != null) {
				int topTenResultsSize = topTenResults.size();
				allKeywordsFound.addAll(topTenResults);
				topTenResults.remove(term);
				topTenResults.stream().forEach(s -> toBeSearched.add(s));
				if (topTenResultsSize < 10) {
					// if there is less than 10 results 
					// there is no need to probe for other values.
					continue;
				}
			} else {
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
						if (searchResults != null) {
							allKeywordsFound.addAll(searchResults);
							searchResults.remove(probeTerm);
							searchResults.stream().forEach(s -> toBeSearched.add(s));
						}
						return null;
					});
				}
			}
		}
		if (toBeSearched != null)
			allKeywordsFound.addAll(toBeSearched);

		synchronized (allKeywordsFound) {
			for (String key : allKeywordsFound) {
				System.out.println(key + " ");
			}
		}

		log.info("Invoked " + nRequests + " to the amazon completion API and found " + allKeywordsFound.size()
				+ " unique results.");
		return allKeywordsFound;
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

		return searchResults;
	}

}
