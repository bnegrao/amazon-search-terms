package com.bnegrao.amazonsearchterms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class AmazonAPIServiceSyncImpl implements AmazonAPIService {

	protected static final Logger log = LoggerFactory.getLogger(AmazonAPIServiceSyncImpl.class);

	private RestTemplate restTemplate;

	private HttpComponentsClientHttpRequestFactory requestFactory;

	public AmazonAPIServiceSyncImpl() {
		init();
	}

	private void init() {
		// Setting Jackson2 as the MessageConverter for all content types. That's
		// necessary because the completion API sends "Content-Type:
		// text/javascript;charset=UTF-8"
		// instead of "application/json"
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
		messageConverters.add(converter);

		requestFactory = getClientHttpRequestFactory();
		restTemplate = new RestTemplate(requestFactory);
		restTemplate.setMessageConverters(messageConverters);
	}

	private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(DEFAULT_REQUEST_TIMEOUT_MILISECONDS);
		return clientHttpRequestFactory;
	}
	

	/* (non-Javadoc)
	 * @see com.bnegrao.amazonsearchterms.service.AmazonAPIService#callAmazonApi(java.lang.String, int)
	 */	
	@SuppressWarnings("unchecked")
	public List<String> callAmazonApi (String prefix, int timeout) {
		if (timeout < 0) {
			return null;
		}

		requestFactory.setConnectTimeout(timeout);
		requestFactory.setReadTimeout(timeout);

		ArrayList<Object> json = null;

		try {
			log.info("Search: [" + prefix + "]");
			json = restTemplate.getForObject(COMPLETION_SERVICE_URL + prefix, ArrayList.class);
			//log.info(json.toString());
		} catch (ResourceAccessException e) {
			log.error("request for prefix '" + prefix + "' aborted due to timeout.");
			return null;
		}


		List<String> searchResults = null;

		if (json != null) {
			ArrayList<Object> response = (ArrayList<Object>) json;
			if (response.size() > 2) {
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
	
	
	/* (non-Javadoc)
	 * @see com.bnegrao.amazonsearchterms.service.AmazonAPIService#recursiveSearch(java.lang.String, long)
	 */
	@Override
	public Set<String> recursiveSearch(String keyword, long timeout) throws InterruptedException, ExecutionException {		
		Set<String> allTerms = new TreeSet<String>();
		LinkedList<String> toBeSearched = new LinkedList<String>();
		toBeSearched.add(keyword);
		int nRequests = 0;
		
		while (toBeSearched.size() > 0 && new Date().getTime() < timeout) {
			String term = toBeSearched.removeFirst();
			if (allTerms.contains(term)) {
				// don't want to repeat requests for terms already searched!
				continue;
			}
			
			List<String> searchResults = callAmazonApi(term, (int) timeout);
			allTerms.add(term);
			nRequests++;
			toBeSearched.addAll(searchResults);
		}		
		
		allTerms.addAll(toBeSearched);		
		
		log.info("Invoked "+ nRequests + " to the amazon completion API and found " + allTerms.size() + " unique results.");
		return allTerms;	
	}	
}
