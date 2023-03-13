package com.spring.LearnRESTapi.SurveyTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class SurveyResourceIntegrationTest {
	
	@Autowired
	private TestRestTemplate template;
	
	private String SPECIFIC_QUESTION_URL = "/surveys/Survey1/questions/Question1";
	private String GENERIC_QUESTIONS_URL = "/surveys/Survey1/questions";
	
	@Test
	public void retrieveSpecificSurveyQuestion() throws JSONException {
		
		HttpHeaders headers = headerContentAndAuth();

		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> responseEntity = template.exchange(SPECIFIC_QUESTION_URL,HttpMethod.GET, httpEntity, String.class);
		
		String expectedResult = """
					{
					    "id": "Question1",
					    "description": "Most Popular Cloud Platform Today",
					    "correctAnswer": "AWS"
					}
				""";
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
		assertEquals("application/json",responseEntity.getHeaders().get("Content-Type").get(0));
		JSONAssert.assertEquals(expectedResult, responseEntity.getBody(), false);
		
	}
	
	@Test
	public void retrieveAllSurveyQuestions() throws JSONException {
		
		HttpHeaders headers = headerContentAndAuth();

		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> responseEntity = template.exchange(GENERIC_QUESTIONS_URL,HttpMethod.GET, httpEntity, String.class);
		
		String expectedResult = """
				[
								    {
								        "id": "Question1"
								    },
								    {
								        "id": "Question2"
								    },
								    {
								        "id": "Question3"
								    }
				]
				""";
		
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
		assertEquals("application/json",responseEntity.getHeaders().get("Content-Type").get(0));
		JSONAssert.assertEquals(expectedResult, responseEntity.getBody(), false);
		
	}
	
	@Test
	void addNewQuestion() {
		
		String requestBody = """
				
			    {
			 
			        "description": "Most Popular Programming Language",
			        "options": [
			            "Java",
			            "Python",
			            "Go",
			            "Groovy"
			        ],
			        "correctAnswer": "Java"
			    }
	
	
	""";
		
		
		HttpHeaders headers = headerContentAndAuth();
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody, headers);
		
		ResponseEntity<String> responseEntity = 
							template.exchange(GENERIC_QUESTIONS_URL,HttpMethod.POST, httpEntity, String.class);
		
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
		String locationHeader = responseEntity.getHeaders().get("Location").get(0);
		assertTrue(locationHeader.contains("/surveys/Survey1/questions"));
		
		//If this test runs first second test will fail, so deleting the post entity
		ResponseEntity<String> responseEntityDelete = template.exchange(locationHeader,HttpMethod.DELETE, httpEntity, String.class);
		assertTrue(responseEntityDelete.getStatusCode().is2xxSuccessful());
	}

	private HttpHeaders headerContentAndAuth() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "Basic "+performBasicAuthEncoding("manja", "dummy"));
		return headers;
	}
	
	private String performBasicAuthEncoding(String user, String password) {
		String combined = user+":"+password;
		String encodedString = Base64.encodeBase64String(combined.getBytes());
		return encodedString;
	}

}
