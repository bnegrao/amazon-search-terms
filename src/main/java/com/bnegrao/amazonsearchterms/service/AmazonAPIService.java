package com.bnegrao.amazonsearchterms.service;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class AmazonAPIService {

	@Value("${completion.api.url}")
	private String COMPLETION_SERVICE_URL;

	private static final Logger log = LoggerFactory.getLogger(AmazonAPIService.class);

	private RestTemplate restTemplate;

	private Integer nRequests;

	@Autowired
	public AmazonAPIService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	// all keywords that were retrieved from the completion api
	Set<String> allTerms = new TreeSet<String>();
	// keywords retrieved from the completion api that were not searched
	// individually
	LinkedList<String> toBeSearched = new LinkedList<>();

	public Set<String> recursiveSearch(String keyword, long timeout) throws InterruptedException, ExecutionException {
		allTerms.clear();
		toBeSearched.clear();

		toBeSearched.add(keyword);
		nRequests = 0;

		while (toBeSearched.size() > 0) {
			if (new Date().getTime() > timeout) {
				break;
			}

			String term = toBeSearched.removeFirst();

			// get the topTen results in a synchronous request
			List<String> topTenResults = callAmazonApi(term, timeout);
			if (topTenResults != null) {			
				allTerms.addAll(topTenResults);
				topTenResults.remove(keyword);
				toBeSearched.addAll(topTenResults);
			} else {
				continue;
			}

			// probe for other results with asynchronous requests
			// if the keyword has has than 4 terms, like "canon camera x16"
			if (term.split("\\s+").length < 4) {
				char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
				List<Character> charsList = getCharList(chars);
				charsList.parallelStream().forEach(c -> {
					if (new Date().getTime() > timeout) {
						return;
					}
					String probeTerm = term + " " + c;
					List<String> searchResults = callAmazonApi(probeTerm, timeout);
					if (searchResults != null) {
						allTerms.addAll(searchResults);
						searchResults.remove(probeTerm);
						toBeSearched.addAll(searchResults);
						
					}
					return;
				});

			}

		}
		if (toBeSearched != null)
			allTerms.addAll(toBeSearched);

		for (String term : allTerms) {
			System.out.println(term + " ");
		}

		log.info("Invoked " + nRequests + " to the amazon completion API and found " + allTerms.size()
				+ " unique results.");
		return allTerms;
	}

	private List<Character> getCharList(char[] charArray) {
		List<Character> list = new LinkedList<>();
		for (char c : charArray) {
			list.add(c);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bnegrao.amazonsearchterms.service.AmazonAPIService#callAmazonApi(java.
	 * lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	public List<String> callAmazonApi(String prefix, long timeout) {
		if (timeout < 0) {
			return null;
		}

		ArrayList<Object> json = null;

		try {
			log.info("Search: [" + prefix + "]");
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

		if (searchResults == null) {
			searchResults = new ArrayList<String>();
		}

		return searchResults;
	}

}
