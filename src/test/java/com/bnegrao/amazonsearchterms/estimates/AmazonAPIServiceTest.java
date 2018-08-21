package com.bnegrao.amazonsearchterms.estimates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.bnegrao.amazonsearchterms.AmazonSearchTermsApplication;
import com.bnegrao.amazonsearchterms.RestTemplateTestConfiguration;
import com.bnegrao.amazonsearchterms.service.AmazonAPIService;


/**
 * These tests don't call the amazon api, they use a mocked RestTemplate object instead.
 * 
 * @author bnegrao
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AmazonSearchTermsApplication.class, AmazonAPIService.class, RestTemplateTestConfiguration.class})
public class AmazonAPIServiceTest {
	
	@Value( "${completion.api.url}" )
	private String COMPLETION_SERVICE_URL;
	
	@Autowired
	AmazonAPIService amazonApiService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Test
	public void recursiveSearchTest() throws InterruptedException, ExecutionException {
		Map<String, List<String>> data = new HashMap<>();
		addTo(data, "canon", "canon camera", "canon lens", "canon t6i" );
		addTo(data, "canon camera", "canon camera bag", "canon camera strap");
		addTo(data, "canon lens", "canon lens cap", "canon lens hood");
		addTo(data, "canon a", "canon accessories");
		addTo(data, "canon b", "canon battery", "canon bag");
		
		makeMocks(data);
		
		Set<String> expectedResults = convertMapValuesToSet(data);		
			
		Set<String> result = amazonApiService.recursiveSearch("canon", new Date().getTime() + 1000000000l);
		
		Assert.assertEquals(expectedResults, result);				
	}
	

	Set<String> convertMapValuesToSet(Map<String, List<String>> data) {
		Set<String> set = new TreeSet<>();
		for (String key: data.keySet()) {
			set.addAll(data.get(key));
		}
		return set;
		
	}
	
	private void addTo(Map<String, List<String>> data, String prefix, String... keywords) {
		List<String> list = Arrays.asList(keywords);
		data.put(prefix, list);
	}
	
	
	@Test
	public void testNoResults() throws InterruptedException, ExecutionException {
		
		Set<String> result = amazonApiService.recursiveSearch("harry potter", new Date().getTime() + 1000000000l);
		
		Assert.assertTrue(result.isEmpty());
	}

	private void makeMocks(Map<String, List<String>> data) {
		
		for (String prefix: data.keySet()) {
			Mockito.when(restTemplate.getForObject(COMPLETION_SERVICE_URL + prefix,
					ArrayList.class)).thenReturn(createAmazonLikeObject(prefix, data.get(prefix)));				
		}				
	}


	/**
	 * creates an object like the one created from converting amazon's json to object
	 * @param prefix
	 * @param subTerms
	 * @return
	 */
	private ArrayList<Object> createAmazonLikeObject(String prefix, List<String> subTerms) {
		ArrayList<Object> list = new ArrayList<Object>();
		ArrayList<String> sublist = new ArrayList<>();
		list.add(prefix);
		for (String term : subTerms) {
			sublist.add(term);
		}
		list.add(sublist);
		return list;		
	}
	
	
	

}
