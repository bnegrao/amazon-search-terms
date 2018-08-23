package com.bnegrao.amazonsearchterms.estimates;

public class EstimationResponse {

	private String keyword;
	private int score;	

	public EstimationResponse(String keyword, int score) {
		super();
		this.keyword = keyword;
		this.score = score;
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

}
