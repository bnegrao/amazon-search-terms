package com.bnegrao.amazonsearchterms.estimates;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
		Set<String> a = new TreeSet<String>();
		a.addAll(makeMock("harry potter", "", " books", " gifts", " lego"));
		a.addAll(makeMock("harry potter books", " first journey", " second journey"));
		a.addAll(makeMock("harry potter gifts", " hat", " rabbit"));
		a.addAll(makeMock("harry potter a", "bracadabra"));
		a.addAll(makeMock("harry potter b", "oomerang", "ooks"));
		
		Set<String> result = amazonApiService.recursiveSearch("harry potter", new Date().getTime() + 1000000000l);
		
		Assert.assertEquals(a, result);				
	}
	
	@Test
	public void testNoResults() throws InterruptedException, ExecutionException {
		
		Set<String> result = amazonApiService.recursiveSearch("harry potter", new Date().getTime() + 1000000000l);
		
		Assert.assertTrue(result.isEmpty());
	}

	@SuppressWarnings("unchecked")
	private List<String> makeMock(String prefix, String... subTerms) {		
		Mockito.when(restTemplate.getForObject(COMPLETION_SERVICE_URL + prefix,
				ArrayList.class)).thenReturn(makeListOf(prefix, subTerms));		
		
		return (ArrayList<String>)makeListOf(prefix, subTerms).get(1);
	}


	/**
	 * creates an object like the one created from converting amazon's json to object
	 * @param prefix
	 * @param subTerms
	 * @return
	 */
	private ArrayList<Object> makeListOf(String prefix, String... subTerms) {
		ArrayList<Object> list = new ArrayList<Object>();
		ArrayList<String> sublist = new ArrayList<>();
		list.add(prefix);
		for (String term : subTerms) {
			sublist.add(prefix + term);
		}
		list.add(sublist);
		return list;		
	}
	
	
	

}
