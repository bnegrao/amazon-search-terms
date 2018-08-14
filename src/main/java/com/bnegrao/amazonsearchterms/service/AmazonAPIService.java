package com.bnegrao.amazonsearchterms.service;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface AmazonAPIService {

	String COMPLETION_SERVICE_URL = "http://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=";
	int DEFAULT_REQUEST_TIMEOUT_MILISECONDS = 1000;

	Set<String> recursiveSearch(String keyword, long l) throws InterruptedException, ExecutionException;

}