package com.bnegrao.amazonsearchterms.service;

import java.util.Set;

public class AmazonApiSearchResult {
	
	private int numberOfKeywordsFound;
	
	private int numberOfRequests;
	
	private int numberOfRequestsWithNoResults;

	private boolean searchInterruptedDueToTimeout;
	
	private int keywordsNotSearchedDueToTimeout;
	
	private long timeElapsedMilis;
	
	private Set<String> keywordsList;	


	public AmazonApiSearchResult(Set<String> keywordsFound, int numberOfRequests,
			Integer nRequestsNoResults, boolean searchInterruptedDueToTimeout, int keywordsNotSearchedDueToTimeout, long timeElapsedMilis) {
		super();
		this.keywordsList = keywordsFound;
		this.numberOfRequests = numberOfRequests;
		this.numberOfRequestsWithNoResults = nRequestsNoResults;
		this.searchInterruptedDueToTimeout = searchInterruptedDueToTimeout;
		this.keywordsNotSearchedDueToTimeout = keywordsNotSearchedDueToTimeout;
		this.timeElapsedMilis = timeElapsedMilis;
		this.numberOfKeywordsFound = keywordsFound.size();
	}


	public int getNumberOfKeywordsFound() {
		return numberOfKeywordsFound;
	}

	public int getNumberOfRequests() {
		return numberOfRequests;
	}
	
	public int getNumberOfRequestsWithNoResults() {
		return numberOfRequestsWithNoResults;
	}	

	public boolean isSearchInterruptedDueToTimeout() {
		return searchInterruptedDueToTimeout;
	}

	public int getKeywordsNotSearchedDueToTimeout() {
		return keywordsNotSearchedDueToTimeout;
	}

	public long getTimeElapsedMilis() {
		return timeElapsedMilis;
	}
	
	public Set<String> getKeywordsList() {
		return keywordsList;
	}
		

}
