package com.api.impactanalysis.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.api.impactanalysis.common.CommonUtils;
import com.api.impactanalysis.common.Constants;
import com.api.impactanalysis.security.config.Configurations;
import com.api.impactanalysis.security.exceptions.DataNotFound;

@Service
public class JohnHopkinsCoidDataSourceImpl implements CovidDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenService.class);
	private final RestTemplate restTemplate;
	private final Configurations configurations;

	@Autowired
	public JohnHopkinsCoidDataSourceImpl(RestTemplate restTemplate, Configurations configurations) {
		this.restTemplate = restTemplate;
		this.configurations = configurations;
	}

	@Override
	public Map<Date, Map<String, Long>> getCasesData() throws DataNotFound {
		Map<Date, Map<String, Long>> dateWiseCasesData = new ConcurrentHashMap<>();
		Optional<ResponseEntity<String>> optionalResponseEntiry = callURL();
		ResponseEntity<String> csvData = optionalResponseEntiry.orElseThrow(() -> new DataNotFound(String.format("Unable to get data from URL: ", configurations.getCovidDataSourceURL())));
		Optional<String> responseBody = Optional.ofNullable(csvData.getBody());
		if (HttpStatus.OK == csvData.getStatusCode()) {
			LOGGER.info("Going to process tokenize and process data.");
			processData(responseBody.orElseThrow(()-> new DataNotFound("URL did return with Success but no body found in response.")), dateWiseCasesData);
			LOGGER.info("Data successfully processed.");
		}
		return dateWiseCasesData;
	}

	private Optional<ResponseEntity<String>> callURL() throws DataNotFound {
		Optional<ResponseEntity<String>> responseEnity = Optional.empty();
		try {
			LOGGER.info(String.format("Going to call URL[%s] to get CSV data.", configurations.getCovidDataSourceURL()));
			responseEnity = Optional.ofNullable(restTemplate.getForEntity(configurations.getCovidDataSourceURL(), String.class));
			LOGGER.info("Rest Template returned.");
		} catch (RestClientException e) {
			LOGGER.error(String.format("Unable to fetch data due to: %s", e.getMessage()), e);
		}
		return responseEnity;
	}

	private void processData(String responseBody, Map<Date, Map<String, Long>> dateWiseCasesData) {
		List<String> lines = CommonUtils.tokenizeToList(responseBody, "\n", false, "(?!(([^\"]*\"){2})*[^\"]*$),");
		if (lines.size() > 1) {
			List<String> headerTokens = CommonUtils.tokenizeToList(lines.get(0), ",", false, null);
			for (int i = 1; i < lines.size(); i++) {
				LOGGER.info(String.format("Processing lineNo[%s]", i));
				List<String> tokensOfOneLine = CommonUtils.tokenizeToList(lines.get(i), ",", false, null);
				processSingleLineTokens(headerTokens, tokensOfOneLine, dateWiseCasesData);
			}
		}

	}

	private void processSingleLineTokens(List<String> headerTokens, List<String> tokensOfOneLine, Map<Date, Map<String, Long>> dateWiseCasesData) {
		try {
			if (headerTokens.size() < 5 || tokensOfOneLine.size() < 5) {
				LOGGER.info(String.format("Ignoring token line as it do not have minimal number of tokens. [%s], headerSize[%s], TokensInLine[%s]", tokensOfOneLine, headerTokens.size(), tokensOfOneLine.size()));
				return;
			}
			String country = tokensOfOneLine.get(1);
			for (int index = 4; index < tokensOfOneLine.size() && index < headerTokens.size(); index++) {
				Date date = CommonUtils.parseToDate(Constants.DATE_FORMAT_MM_DD_YY, headerTokens.get(index));
				Long countOnDate = Long.parseLong(tokensOfOneLine.get(index));
				Map<String, Long> countryCount = dateWiseCasesData.get(date);
				if (null == countryCount) {
					countryCount = new ConcurrentHashMap<>();
					dateWiseCasesData.put(date, countryCount);
				}
				countryCount.put(country, countryCount.getOrDefault(country, 0L) + countOnDate);
			}
		} catch (Exception e) {
			LOGGER.warn(String.format("Could not process row with tokens %s due to %s", tokensOfOneLine, e.getMessage()), e);
		}
	}
}
