package com.api.impactanalysis.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api.impactanalysis.repository.InMemoryParsedData;
import com.api.impactanalysis.security.exceptions.DataNotFound;
import com.api.impactanalysis.service.CovidDataSource;
import com.opencsv.exceptions.CsvValidationException;

@Component
public class ScheduledTasks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    private final CovidDataSource covidDataSource;
    private final InMemoryParsedData inMemoryParsedData;

    @Autowired
    public ScheduledTasks(CovidDataSource covidDataSource, InMemoryParsedData inMemoryParsedData) {
        this.covidDataSource = covidDataSource;
        this.inMemoryParsedData = inMemoryParsedData;
    }

    @Scheduled(fixedDelayString = "${api.impactnalysis.settings.dataRefreshInterval}")
    public void refreshImpactAnalysisData() throws CsvValidationException, IOException {
        try {
            LOGGER.info("ScheduledTasks started to fetch data from configured URL");
            Map<Date, Map<String, Long>> casesData = covidDataSource.getCasesData();
            LOGGER.info("Data fetched successfully");
            List<Date> keys = Collections.synchronizedList(new ArrayList<>(casesData.keySet()));
            LOGGER.info("Going to sort date keys.");
            Collections.sort(keys);
            LOGGER.info("Keys sorted.");
            inMemoryParsedData.setDateWiseCasesData(casesData);
            inMemoryParsedData.setSortedKeys(keys);
        } catch (DataNotFound e) {
            LOGGER.info("Data not found due to: ", e);
        }
    }
}
