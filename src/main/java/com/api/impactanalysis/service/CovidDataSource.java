package com.api.impactanalysis.service;

import java.util.Date;
import java.util.Map;

import com.api.impactanalysis.security.exceptions.DataNotFound;

public interface CovidDataSource {
	Map<Date, Map<String, Long>> getCasesData() throws DataNotFound;
}
