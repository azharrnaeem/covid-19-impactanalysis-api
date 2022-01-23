package com.api.impactanalysis.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.api.impactanalysis.security.exceptions.DataNotFound;
import com.opencsv.exceptions.CsvValidationException;

public interface CovidDataSource {
    Map<Date, Map<String, Long>> getCasesData() throws DataNotFound, CsvValidationException, IOException;

    Date maxDateWithDataAvailable();
}
