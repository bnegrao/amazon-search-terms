package com.bnegrao.amazonsearchterms.estimates;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.bnegrao.amazonsearchterms.service.AmazonAPIService;
import com.bnegrao.amazonsearchterms.service.AmazonAPIServiceAsyncImpl;
import com.bnegrao.amazonsearchterms.service.AmazonAPIServiceSyncImpl;

public class AmazonAutocompleteServiceTest {
	
	AmazonAPIServiceAsyncImpl autocompleteService = new AmazonAPIServiceAsyncImpl();

	@Test
	public void searchTest() throws InterruptedException, ExecutionException {
		CompletableFuture<List<String>> terms = autocompleteService.callAmazonAPIAsync("canon", 111111);	
		terms.join();
		assertTrue(terms.get() != null);
		assertTrue(terms.get().size() == 10);
	}

}
