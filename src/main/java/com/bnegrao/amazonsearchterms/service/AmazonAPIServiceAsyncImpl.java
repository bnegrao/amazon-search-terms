package com.bnegrao.amazonsearchterms.service;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AmazonAPIServiceAsyncImpl extends AmazonAPIServiceSyncImpl {

	private static final Logger log = LoggerFactory.getLogger(AmazonAPIServiceAsyncImpl.class);
	
	private Integer nRequests;


	@Override
	public Set<String> recursiveSearch(String keyword, long timeout) throws InterruptedException, ExecutionException {
		Set<String> allTerms = new TreeSet<String>();		
		LinkedList<String> toBeSearched = new LinkedList<String>();

		toBeSearched.add(keyword);
		nRequests = 0;

		while (toBeSearched.size() > 0 && new Date().getTime() < timeout) {
			String term = toBeSearched.removeFirst();
			if (allTerms.contains(term)) {
				// don't want to repeat requests for terms already searched!
				continue;
			}

			// get the topTen results in a synchronous request
			List<String> topTenResults = callAmazonApi(term, (int)timeout);
			if (topTenResults != null && topTenResults.size() > 0) {
				toBeSearched.addAll(topTenResults);
				allTerms.add(term);
			}
			nRequests++;

			// probe for other results with asynchronous requests
			String[] tokens = term.split("\\s+");
			if (tokens.length < 4) {
				for (char someChar = 'a'; someChar <= 'z' && new Date().getTime() < timeout ; someChar++) {
					String probeTerm = term + " " + someChar;
					if (probeTerm.toString().equals("harry potter and the chamber of secrets a")) {
						System.out.println("Stop!");
					}
					CompletableFuture<List<String>> probeNewTerms = CompletableFuture.supplyAsync(() -> {
						return callAmazonApi(probeTerm,(int) timeout);
					});

					probeNewTerms.thenApply(searchResults -> { 
						if (searchResults != null) {
							if (!searchResults.isEmpty()) {
								toBeSearched.addAll(searchResults);
							}
							nRequests++;
						}						
						return null;
					});
				}
			}


		}
		if (toBeSearched != null && toBeSearched.size() > 0) 
			allTerms.addAll(toBeSearched);

		for (String term: allTerms) {
			System.out.println(term + " ");
		}
		
		
//		
		log.info("Invoked " + nRequests + " to the amazon completion API and found " + allTerms.size()
				+ " unique results.");
		return allTerms;
	}

}
