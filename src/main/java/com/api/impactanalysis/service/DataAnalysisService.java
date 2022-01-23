package com.api.impactanalysis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.impactanalysis.common.CommonUtils;
import com.api.impactanalysis.common.Constants;
import com.api.impactanalysis.model.CasesInfo;
import com.api.impactanalysis.model.CountryWiseCases;
import com.api.impactanalysis.model.DatewiseCountryData;
import com.api.impactanalysis.model.NewCasesToday;
import com.api.impactanalysis.repository.InMemoryParsedData;

@Service
public class DataAnalysisService {
    private final InMemoryParsedData inMemoryParsedData;

    @Autowired
    public DataAnalysisService(InMemoryParsedData inMemoryParsedData) {
        this.inMemoryParsedData = inMemoryParsedData;
    }

    public NewCasesToday getNewCases(Date requestedDate) {
        NewCasesToday newCasesInfo = new NewCasesToday();
        List<Date> keyList = inMemoryParsedData.getSortedKeys();
        int dateIndex = Collections.binarySearch(keyList, requestedDate);
        if (dateIndex < 0) {
            newCasesInfo.setDescription(String.format("Right now data is not available for date: %s",
                    CommonUtils.formatDate(Constants.DATE_FORMAT_MM_DD_YY, requestedDate)));
            return newCasesInfo;
        }
        Map<String, Long> dataOnRequestedDate = inMemoryParsedData.getDateWiseCasesData().getOrDefault(requestedDate, new ConcurrentHashMap<>());
        long sumToday = dataOnRequestedDate.values().stream().mapToLong(Long::longValue).sum();
        long sumBefore = 0L;
        if (dateIndex > 0) {
            sumBefore = inMemoryParsedData.getDateWiseCasesData().get(keyList.get(dateIndex - 1)).values().stream().mapToLong(Long::longValue).sum();
        }
        long change = sumToday - sumBefore;
        newCasesInfo.setGloballyReportedNewCases(change < 0 ? 0 : change);
        newCasesInfo.setDescription(change <= 0 ? "No new cases reported." : "Globally new cases are reported.");
        return newCasesInfo;
    }

    public CountryWiseCases getNewCasesCountrywise(Date requestedDate) {
        CountryWiseCases countryWiseCases = new CountryWiseCases();
        List<Date> keyList = inMemoryParsedData.getSortedKeys();
        Map<String, Long> dataOnRequestedDate = inMemoryParsedData.getDateWiseCasesData().getOrDefault(requestedDate, new ConcurrentHashMap<>());
        if (dataOnRequestedDate.size() == 0) {
            countryWiseCases.setDescription(String.format("Right now data is not available for date: %s",
                    CommonUtils.formatDate(Constants.DATE_FORMAT_MM_DD_YY, requestedDate)));
            return countryWiseCases;
        }
        List<CasesInfo> caseInfoList = dataOnRequestedDate.entrySet().stream().map(entry -> new CasesInfo(entry.getKey(), entry.getValue())).collect(
                Collectors.toList());
        Date lastUpdatedDateOfDataRefereshedData = getLastUpdatedDateBefore(requestedDate, keyList);
        if (requestedDate.compareTo(lastUpdatedDateOfDataRefereshedData) != 0) {
            Map<String, Long> previousData = inMemoryParsedData.getDateWiseCasesData().get(lastUpdatedDateOfDataRefereshedData);
            caseInfoList.forEach(caseInfo -> caseInfo.setCases(caseInfo.getCases() - previousData.getOrDefault(caseInfo.getCountryName(), 0L)));
        }
        List<CasesInfo> sortedDataList = caseInfoList.stream().filter(caseInfo -> caseInfo.getCases() >= 0).sorted(
                Comparator.comparing(CasesInfo::getCases).reversed()).collect(Collectors.toList());
        countryWiseCases.setCountryWiseCasesData(sortedDataList);
        countryWiseCases.setDate(requestedDate);
        return countryWiseCases;
    }

    public CasesInfo getNewCasesReportedInCountry(String country) {
        Optional<List<CasesInfo>> countryWiseDataForToday = Optional.ofNullable(
                getNewCasesCountrywise(maxDateForWhichDataIsAvailable()).getCountryWiseCasesData());
        Optional<CasesInfo> requestCountryData = countryWiseDataForToday.orElse(new ArrayList<CasesInfo>(0)).stream().filter(
                caseInfo -> country.equalsIgnoreCase(caseInfo.getCountryName())).findAny();
        return requestCountryData.orElse(new CasesInfo(country, 0L));
    }

    public CountryWiseCases getTopNCountriesWithNewCases(int count) {
        CountryWiseCases countryWiseCases = new CountryWiseCases();
        Optional<List<CasesInfo>> countryWiseDataForToday = Optional.ofNullable(
                getNewCasesCountrywise(inMemoryParsedData.getMaxDate()).getCountryWiseCasesData());
        List<CasesInfo> topNCountriesData = countryWiseDataForToday.orElse(new ArrayList<CasesInfo>()).stream().limit(count).collect(
                Collectors.toList());
        countryWiseCases.setCountryWiseCasesData(topNCountriesData);
        countryWiseCases.setDescription(topNCountriesData.size() > 0 ? null : "No data found.");
        return countryWiseCases;
    }

    public DatewiseCountryData getCasesDataInCountrySinceDate(String country, Date referenceDate) {
        DatewiseCountryData datewiseCountryData = new DatewiseCountryData();
        if (!inMemoryParsedData.getDateWiseCasesData().containsKey(referenceDate)
                || !inMemoryParsedData.getDateWiseCasesData().get(referenceDate).containsKey(country)) {
            datewiseCountryData.setDescription("Data not found for given date or country.");
            return datewiseCountryData;
        }
        List<CasesInfo> dateWiseNewCases = new ArrayList<>();
        List<Date> keyList = inMemoryParsedData.getSortedKeys();
        int indexOfReferenceDate = Collections.binarySearch(keyList, referenceDate);
        Long previousDateCount = 0L;
        if (indexOfReferenceDate > 0) {
            previousDateCount = inMemoryParsedData.getDateWiseCasesData().get(keyList.get(indexOfReferenceDate - 1)).get(country);
        }
        for (int i = indexOfReferenceDate; i < keyList.size(); i++) {
            Date date = keyList.get(i);
            Long count = Optional.ofNullable(inMemoryParsedData.getDateWiseCasesData().get(date).get(country)).orElse(0L);
            dateWiseNewCases.add(new CasesInfo(null, count - previousDateCount, CommonUtils.formatDate(Constants.DATE_FORMAT_MM_DD_YY, date)));
            previousDateCount = count;
        }
        datewiseCountryData.setDateWiseNewCases(dateWiseNewCases);
        datewiseCountryData.setCountry(country);
        datewiseCountryData.setReferenceDate(CommonUtils.formatDate(Constants.DATE_FORMAT_MM_DD_YY, referenceDate));
        return datewiseCountryData;
    }

    private Date getLastUpdatedDateBefore(Date date, List<Date> sortedList) {
        int binarySearch = Collections.binarySearch(sortedList, date);
        return binarySearch > 0 ? sortedList.get(binarySearch - 1) : date;
    }

    public Date maxDateForWhichDataIsAvailable() {
        return inMemoryParsedData.getMaxDate();
    }

}
