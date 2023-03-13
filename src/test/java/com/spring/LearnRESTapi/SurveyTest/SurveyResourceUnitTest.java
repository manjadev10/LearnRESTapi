package com.spring.LearnRESTapi.SurveyTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.spring.LearnRESTapi.Survey.Question;
import com.spring.LearnRESTapi.Survey.SurveyResource;
import com.spring.LearnRESTapi.Survey.SurveyService;

@WebMvcTest(controllers = SurveyResource.class)
@AutoConfigureMockMvc(addFilters = false)
public class SurveyResourceUnitTest {
	
	@MockBean
	private SurveyService surveyService;
	
	@Autowired
	private MockMvc mockMvc;
	
	private static String SPECIFIC_QUESTION_URL = "http://localhost:8080/surveys/Survey1/questions/Question1";
	private static String GENERIC_QUESTIONS_URL = "http://localhost:8080/surveys/Survey1/questions";
	
	
	@Test
	public void retrieveSpecificSurveyQuestion_404Scenario() throws Exception {
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);
		
		//In this scenario surveyService will not return anything as we havent yet stub the method required
		MvcResult  mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void retrieveSpecificSurveyQuestion_basicScenario() throws Exception {
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);
		
		Question question = new Question("Question1", "Most Popular Cloud Platform Today",
				Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");
		
		String expectedResult = """
				{
				    "id": "Question1",
				    "description": "Most Popular Cloud Platform Today",
				    "correctAnswer": "AWS"
				}
			""";		
		
		when(surveyService.retrieveSpecificSurveyQuestion("Survey1", "Question1")).thenReturn(question);
		MvcResult  mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = mvcResult.getResponse();
		JSONAssert.assertEquals(expectedResult, response.getContentAsString(), false);
		assertEquals(200, response.getStatus());
	}
	
	
	@Test
	public void retrieveAllSurveyQuestions() throws Exception {
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GENERIC_QUESTIONS_URL).accept(MediaType.APPLICATION_JSON);
		
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
		
		Question question1 = new Question("Question1", "Most Popular Cloud Platform Today",
				Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");
		Question question2 = new Question("Question2", "Fastest Growing Cloud Platform",
				Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "Google Cloud");
		Question question3 = new Question("Question3", "Most Popular DevOps Tool",
				Arrays.asList("Kubernetes", "Docker", "Terraform", "Azure DevOps"), "Kubernetes");

		List<Question> questions = new ArrayList<>(Arrays.asList(question1, question2, question3));
		
		when(surveyService.retrieveAllSurveyQuestions("Survey1")).thenReturn(questions);
		MvcResult  mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = mvcResult.getResponse();
		JSONAssert.assertEquals(expectedResult, response.getContentAsString(), false);
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void addNewSurveyQuestion_basicScenario() throws Exception {
		
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
		
		RequestBuilder requestBuilder = 
						MockMvcRequestBuilders.post(GENERIC_QUESTIONS_URL)
						.accept(MediaType.APPLICATION_JSON).content(requestBody).contentType(MediaType.APPLICATION_JSON);
		
		when(surveyService.addNewSurveyQuestion(anyString(),any())).thenReturn("Question4");
		MvcResult  mvcResult = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(201, mvcResult.getResponse().getStatus());
		String expectedLocation = "http://localhost:8080/surveys/Survey1/questions/Question4";
		assertEquals(expectedLocation, mvcResult.getResponse().getHeader("Location"));
	}
}
