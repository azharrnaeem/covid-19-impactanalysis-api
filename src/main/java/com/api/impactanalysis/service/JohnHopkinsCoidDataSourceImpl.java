package com.api.impactanalysis.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
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
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class JohnHopkinsCoidDataSourceImpl implements CovidDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(JohnHopkinsCoidDataSourceImpl.class);
    private final RestTemplate restTemplate;
    private final Configurations configurations;
    private Date maxDate = null;

    @Autowired
    public JohnHopkinsCoidDataSourceImpl(RestTemplate restTemplate, Configurations configurations) {
        this.restTemplate = restTemplate;
        this.configurations = configurations;
    }

    @Override
    public Map<Date, Map<String, Long>> getCasesData() throws DataNotFound, CsvValidationException, IOException {
        Map<Date, Map<String, Long>> dateWiseCasesData = new ConcurrentHashMap<>();
        Optional<ResponseEntity<String>> optionalResponseEntiry = callURL();
        ResponseEntity<String> csvData = optionalResponseEntiry.orElseThrow(
                () -> new DataNotFound(String.format("Unable to get data from URL: ", configurations.getCovidDataSourceURL())));
        Optional<String> responseBody = Optional.ofNullable(csvData.getBody());
        if (HttpStatus.OK == csvData.getStatusCode()) {
            LOGGER.info("Going to process tokenize and process data.");
            processData(responseBody.orElseThrow(() -> new DataNotFound("URL did return with Success but no body found in response.")),
                    dateWiseCasesData);
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

    private void processData(String responseBody, Map<Date, Map<String, Long>> dateWiseCasesData) throws CsvValidationException, IOException {
        CSVReader csvReader = new CSVReaderBuilder(new StringReader(responseBody)).build();
        String[] headers = csvReader.readNext();
        maxDate = CommonUtils.parseToDate(Constants.DATE_FORMAT_MM_DD_YY, headers[headers.length - 1]);
        String[] dataRow;
        while ((dataRow = csvReader.readNext()) != null) {
            processRow(headers, dataRow, dateWiseCasesData);
        }
    }

    private void processRow(String[] headerTokens, String[] tokensOfOneLine, Map<Date, Map<String, Long>> dateWiseCasesData) {
        try {
            if (headerTokens.length < 5 || tokensOfOneLine.length < 5) {
                LOGGER.info(String.format("Ignoring token line as it does not have minimal number of tokens. [%s], headerSize[%s], TokensInLine[%s]",
                        tokensOfOneLine, headerTokens.length, tokensOfOneLine.length));
                return;
            }
            String country = tokensOfOneLine[1];
            for (int index = 4; index < tokensOfOneLine.length && index < headerTokens.length; index++) {
                Date date = CommonUtils.parseToDate(Constants.DATE_FORMAT_MM_DD_YY, headerTokens[index]);
                Long countOnDate = Long.parseLong(tokensOfOneLine[index]);
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

    @Override
    public Date maxDateWithDataAvailable() {
        return maxDate;
    }
}
