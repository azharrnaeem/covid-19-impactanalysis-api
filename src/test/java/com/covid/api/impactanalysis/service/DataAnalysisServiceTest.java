package com.covid.api.impactanalysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.api.impactanalysis.model.CasesInfo;
import com.api.impactanalysis.model.CountryWiseCases;
import com.api.impactanalysis.model.DatewiseCountryData;
import com.api.impactanalysis.model.NewCasesToday;
import com.api.impactanalysis.repository.InMemoryParsedData;
import com.api.impactanalysis.service.DataAnalysisService;

class DataAnalysisServiceTest {
	private DataAnalysisService dataAnalysisService;

	@Mock
	private InMemoryParsedData inMemoryParsedData;

	@BeforeEach
	void doSetup() {
		MockitoAnnotations.initMocks(this);
		dataAnalysisService = new DataAnalysisService(inMemoryParsedData);
	}

	@Test
	@DisplayName("If Number of cases have decreased from previous data then 0 would be returned.")
	void testNoneCasesIncreasedGloballyToday() {
		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 100L);
		countryWiseCount.put("India", 100L);

		mockData.put(date061020, countryWiseCount);
		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 50L);
		countryWiseCount.put("India", 50L);
		mockData.put(tody, countryWiseCount);

		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		NewCasesToday newCasesToday = dataAnalysisService.getNewCases(null);
		assertNull(newCasesToday.getGloballyReportedNewCases(), "Field should be null as cases have decreased or remain equals.");
	}

	@Test
	@DisplayName("If Number of cases have increased than previous global count then number of increased cases should be returned.")
	void testIncreasedCasesGloballyToday() {
		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 100L);
		countryWiseCount.put("India", 100L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 300L);
		countryWiseCount.put("India", 400L);
		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);
		Mockito.when(inMemoryParsedData.getMaxDate()).thenReturn(tody);
		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		NewCasesToday newCasesToday = dataAnalysisService.getNewCases(tody);
		assertEquals(500, newCasesToday.getGloballyReportedNewCases(), "500 cases increased = 200 in Pakistan, 300 in India");
	}


	@Test
	@DisplayName("Get CountryWise new cases for today")
	void testGetNewCasesCountrywise() {
		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 100L);
		countryWiseCount.put("India", 100L);
		countryWiseCount.put("Belgium", 100L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 300L);
		countryWiseCount.put("India", 400L);
		countryWiseCount.put("Belgium", 0L);
		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);

		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		Mockito.when(inMemoryParsedData.getMaxDate()).thenReturn(tody);
		CountryWiseCases newCasesCountrywise = dataAnalysisService.getNewCasesCountrywise(dataAnalysisService.maxDateForWhichDataIsAvailable());
		assertEquals(2, newCasesCountrywise.getCountryWiseCasesData().size(), "2 Countries have new cases today");
	}

	@Test
	@DisplayName("Check if data returned is sorted in descending order")
	void testSorting() {
		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 100L);
		countryWiseCount.put("India", 100L);
		countryWiseCount.put("Belgium", 100L);
		countryWiseCount.put("Italy", 10L);
		countryWiseCount.put("Germany", 100L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 300L);
		countryWiseCount.put("India", 400L);
		countryWiseCount.put("Belgium", 0L);
		countryWiseCount.put("Italy", 5000L);
		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);

		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		Mockito.when(inMemoryParsedData.getMaxDate()).thenReturn(tody);
		CountryWiseCases newCasesCountrywise = dataAnalysisService.getNewCasesCountrywise(tody);
		assertEquals(3, newCasesCountrywise.getCountryWiseCasesData().size(), "2 Countries have new cases today");
		assertEquals("Italy", newCasesCountrywise.getCountryWiseCasesData().get(0).getCountryName());
		assertEquals("India", newCasesCountrywise.getCountryWiseCasesData().get(1).getCountryName());
		assertEquals("Pakistan", newCasesCountrywise.getCountryWiseCasesData().get(2).getCountryName());
	}


	@Test
	@DisplayName("Get new cases reported today in requested country")
	void testGetNewCasesReportedInCountryToday() {
		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 100L);
		countryWiseCount.put("India", 100L);
		countryWiseCount.put("Belgium", 100L);
		countryWiseCount.put("Italy", 10L);
		countryWiseCount.put("Germany", 100L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 300L);
		countryWiseCount.put("India", 400L);
		countryWiseCount.put("Belgium", 0L);
		countryWiseCount.put("Italy", 5000L);
		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);

		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getMaxDate()).thenReturn(tody);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		CasesInfo newCasesReportedInCountryToday = dataAnalysisService.getNewCasesReportedInCountry("Italy");
		assertEquals(4990, newCasesReportedInCountryToday.getCases(), "5000-10 = 4990 in italy");
	}

	@Test
	@DisplayName("Top N countries with new cases today.")
	void testGetTopNCountriesWithNewCasesToday() {

		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 10L);
		countryWiseCount.put("India", 9L);
		countryWiseCount.put("Belgium", 100L);
		countryWiseCount.put("Italy", 10L);
		countryWiseCount.put("Germany", 100L);
		countryWiseCount.put("Hangary", 50L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 20000L);
		countryWiseCount.put("India", 7000L);
		countryWiseCount.put("Belgium", 5000L);
		countryWiseCount.put("Italy", 8000L);
		countryWiseCount.put("Germany", 15000L);
		countryWiseCount.put("Hangary", 100L);
		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);

		Mockito.when(inMemoryParsedData.getMaxDate()).thenReturn(tody);
		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		CountryWiseCases topNCountriesWithNewCasesToday = dataAnalysisService.getTopNCountriesWithNewCases(2);
		assertEquals("Pakistan", topNCountriesWithNewCasesToday.getCountryWiseCasesData().get(0).getCountryName());
		assertEquals(19990, topNCountriesWithNewCasesToday.getCountryWiseCasesData().get(0).getCases());

		assertEquals("Germany", topNCountriesWithNewCasesToday.getCountryWiseCasesData().get(1).getCountryName());
		assertEquals(14900, topNCountriesWithNewCasesToday.getCountryWiseCasesData().get(1).getCases());

	}

	@Test
	void testGetCasesDataInCountrySinceDate() {


		Map<Date, Map<String, Long>> mockData = new HashMap<>();
		Date date061020 = parseToDate("MM/dd/yy", "06/10/20");
		Date tody = getSystemDateWithoutTime();

		Map<String, Long> countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 10L);
		countryWiseCount.put("India", 9L);

		mockData.put(date061020, countryWiseCount);

		countryWiseCount = new HashMap<>();
		countryWiseCount.put("Pakistan", 11L);
		countryWiseCount.put("India", 10L);

		mockData.put(tody, countryWiseCount);

		List<Date> linkedDates = new LinkedList<>();
		linkedDates.add(date061020);
		linkedDates.add(tody);

		Mockito.when(inMemoryParsedData.getDateWiseCasesData()).thenReturn(mockData);
		Mockito.when(inMemoryParsedData.getSortedKeys()).thenReturn(linkedDates);
		DatewiseCountryData casesDataInCountrySinceDate = dataAnalysisService.getCasesDataInCountrySinceDate("Pakistan", date061020);
		assertEquals(1, casesDataInCountrySinceDate.getDateWiseNewCases().get(1).getCases());

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

	public static Date getSystemDateWithoutTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
