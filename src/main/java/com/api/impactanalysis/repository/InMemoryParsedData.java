package com.api.impactanalysis.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InMemoryParsedData {
	private Map<Date, Map<String, Long>> dateWiseCasesData;
	private List<Date> sortedKeys;

	public Map<Date, Map<String, Long>> getDateWiseCasesData() {
		return dateWiseCasesData;
	}

	public void setDateWiseCasesData(Map<Date, Map<String, Long>> dateWiseCasesData) {
		this.dateWiseCasesData = dateWiseCasesData;
	}

	public List<Date> getSortedKeys() {
		return sortedKeys;
	}

	public void setSortedKeys(List<Date> sortedKeys) {
		this.sortedKeys = sortedKeys;
	}

}
