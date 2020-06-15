package com.covid.api.impactanalysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.api.impactanalysis.security.config.Configurations;
import com.api.impactanalysis.security.exceptions.DataNotFound;
import com.api.impactanalysis.service.JohnHopkinsCoidDataSourceImpl;

class JohnHopkinsCoidDataSourceImplTest {
	private JohnHopkinsCoidDataSourceImpl johnHopkinsCoidDataSourceImpl;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private Configurations configurations;

	@Mock
	private ResponseEntity<String> responseEntity;

	@BeforeEach
	void doSetup() {
		MockitoAnnotations.initMocks(this);
		johnHopkinsCoidDataSourceImpl = new JohnHopkinsCoidDataSourceImpl(restTemplate, configurations);
	}

	@Test
	@DisplayName("Test that Implementation of Coivd datasource retreive data from configured URL.")
	void testConfiguredURLIsUsedForData() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Test data");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		johnHopkinsCoidDataSourceImpl.getCasesData();
		verify(configurations, atLeastOnce()).getCovidDataSourceURL();
		verify(configurations, never()).setCovidDataSourceURL(any());
		verify(restTemplate, times(1)).getForEntity(testURI, String.class);
	}

	@Test
	@DisplayName("Test that DatanotFound Exception is thrown if url do return with response body")
	void testDataNotFoundExceptionThrown() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn(null);
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Assertions.assertThrows(DataNotFound.class, () -> {
			johnHopkinsCoidDataSourceImpl.getCasesData();
		});
	}

	@Test
	@DisplayName("Test that RestTemplate if throws Rest client exception then DataNotFound exception gets thrown.")
	void testDataNotFoundExceptionThrownIfRestClientThrowsException() throws URISyntaxException {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn(null);
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenThrow(RestClientException.class);
		Assertions.assertThrows(DataNotFound.class, () -> {
			johnHopkinsCoidDataSourceImpl.getCasesData();
		});
	}

	@Test
	@DisplayName("Test if URL doesn't return with OK http status then an empty map is return.")
	void testEmptyMapIfCallToURLIsNotSuccess() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(0, casesData.size(), "Map should be empty as Http status code from host wasn't OK");
	}

	@Test
	@DisplayName("Test there are less than 5 tokens then map returned is empty.")
	void testEmptyMapOnLessThanFiveTokens() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Token1,Token2,Token3,Token4\nt1,t2,t3,t4");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(0, casesData.size(), "Less than 5 tokens. Hence map should be empty.");
	}

	@Test
	@DisplayName("Test if there is only header in response.")
	void testEmptyMapIfOnlyHeaderInResponse() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Token1,Token2,Token3,Token4,Token5");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(0, casesData.size(), "There is not data in response but header only");
	}

	@Test
	@DisplayName("Test data is parsed correctly.")
	void testDataParsing() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Province/State,Country/Region,Lat,Long,1/22/20\n" + ",Pakistan,30.3753,69.3451,0");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(1, casesData.size(), "There is not data in response but header only");
	}

	@Test
	@DisplayName("Test data is parsed correctly and grouped by date.")
	void testDataDataIsGroupedByDate() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Province/State,Country/Region,Lat,Long,1/22/20\n" 
		+ ",Pakistan,30.3753,69.3451,0\n" 
		+ ",Australia,30.3753,69.3451,0");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Date date = parseToDate("MM/dd/yy", "01/22/20");
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(1, casesData.size(), "There is not data in response but header only");
		assertEquals(2, casesData.get(date).size(), "Two Entries expected in map");
	}
	
	@Test
	@DisplayName("Test that if there is commas between inverted commas of a token then its is paresed as one token. i.e \"South, Korea\" here comma in between name shouldn't considered as token")
	void testDateWithCommasInBetweenNameOfCountryIsParsedCorrectly() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Province/State,Country/Region,Lat,Long,1/22/20\n" 
		+ ",\"South, Korea\",30.3753,69.3451,0\n" 
		+ ",Australia,30.3753,69.3451,0");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Date date = parseToDate("MM/dd/yy", "01/22/20");
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(1, casesData.size(), "There is not data in response but header only");
		assertEquals(2, casesData.get(date).size(), "Two Entries expected in map");
	}
	
	@Test
	@DisplayName("Test if a row contains invalid token then its is skipped and hence not returned in map. i.e instead of nummber a string Avb is given in token")
	void testInvalidRowsAreSkipped() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Province/State,Country/Region,Lat,Long,1/22/20\n" 
		+ ",Pakistan,30.3753,69.3451,0\n" 
		+ ",India,30.3753,69.3451,Avb\n"
		+ ",Australia,30.3753,69.3451,0");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Date date = parseToDate("MM/dd/yy", "01/22/20");
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(1, casesData.size(), "There is not data in response but header only");
		assertEquals(2, casesData.get(date).size(), "Two Entries expected in map");
	}
	
	
	@Test
	@DisplayName("Test that country wise data is maintained for multiple entries of same country for single date")
	void testCountryWiseCount() throws URISyntaxException, DataNotFound {
		URI testURI = new URI("http://localhost/test");
		when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		when(responseEntity.getBody()).thenReturn("Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" 
		+ ",Pakistan,30.3753,69.10,10,300\n" 
		+ ",Pakistan,30.3753,69.20,20\n" 
		+ ",Australia,30.3753,69.3451,15");
		when(configurations.getCovidDataSourceURL()).thenReturn(testURI);
		when(restTemplate.getForEntity(testURI, String.class)).thenReturn(responseEntity);
		Date date012220 = parseToDate("MM/dd/yy", "01/22/20");
		Date date012320 = parseToDate("MM/dd/yy", "01/23/20");
		Map<Date, Map<String, Long>> casesData = johnHopkinsCoidDataSourceImpl.getCasesData();
		assertEquals(30, casesData.get(date012220).get("Pakistan"), "Expected count = 30, dated 01/22/2020");
		assertEquals(15, casesData.get(date012220).get("Australia"), "Expected count = 15, dated 01/22/2020");
		assertEquals(300, casesData.get(date012320).get("Pakistan"), "Expected count = 300, dated 01/23/2020");
		assertNull(casesData.get(date012320).get("Australia"), "Expected null for date = 01/23/2020");
	}

	public Date parseToDate(String format, String date) {
		Date dateObj = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			dateObj = formatter.parse(date);
		} catch (ParseException e) {
		}
		return dateObj;
	}
}
