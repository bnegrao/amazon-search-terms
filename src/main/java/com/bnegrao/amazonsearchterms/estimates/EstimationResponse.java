package com.bnegrao.amazonsearchterms.estimates;

public class EstimationResponse {

	private String keyword;
	private int score;	
	private long timeElapsedMilis;
	
	public EstimationResponse(String keyword, int score, long timeElapsedMilis) {
		super();
		this.keyword = keyword;
		this.score = score;
		this.timeElapsedMilis = timeElapsedMilis;
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

}
