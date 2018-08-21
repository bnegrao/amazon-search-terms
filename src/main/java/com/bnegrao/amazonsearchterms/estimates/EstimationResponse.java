package com.bnegrao.amazonsearchterms.estimates;

import java.util.Set;

public class EstimationResponse {

	private String keyword;
	private int score;	
	private long timeElapsedMilis;
	private Set<String> searchTerms;
	
	public EstimationResponse(String keyword, int score, long timeElapsedMilis, Set<String> searchTerms) {
		super();
		this.keyword = keyword;
		this.score = score;
		this.timeElapsedMilis = timeElapsedMilis;
		this.searchTerms = searchTerms;
	}		
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}



	public long getTimeElapsedMilis() {
		return timeElapsedMilis;
	}



	public void setTimeElapsedMilis(long timeElapsedMilis) {
		this.timeElapsedMilis = timeElapsedMilis;
	}

	public Set<String> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(Set<String> searchTerms) {
		this.searchTerms = searchTerms;
	}

}
