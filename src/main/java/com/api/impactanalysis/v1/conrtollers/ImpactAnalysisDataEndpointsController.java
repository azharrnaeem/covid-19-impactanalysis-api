package com.api.impactanalysis.v1.conrtollers;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.impactanalysis.model.CasesInfo;
import com.api.impactanalysis.model.CountryWiseCases;
import com.api.impactanalysis.model.DatewiseCountryData;
import com.api.impactanalysis.model.NewCasesToday;
import com.api.impactanalysis.service.DataAnalysisService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class ImpactAnalysisDataEndpointsController {
    private final DataAnalysisService impactAnalysisService;

    @Autowired
    public ImpactAnalysisDataEndpointsController(DataAnalysisService impactAnalysisService) {
        this.impactAnalysisService = impactAnalysisService;
    }

    @ApiOperation(value = "Returns new cases reported globally today. Today = UTC time. Optionally date can be passed in request parameter", authorizations = {
            @Authorization(value = "Bearer") })
    @RequestMapping(value = "/api/data/newcases/today", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody NewCasesToday getNewCases(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "MM/dd/yy") Date date,
            @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader)
            throws IOException, ServletException {
        return impactAnalysisService.getNewCases(date);
    }

    @ApiOperation(value = "Returns newly reported cases in all countries till the date for which data is available", authorizations = {
            @Authorization(value = "Bearer") })
    @RequestMapping(value = "/api/data/newcases/countrywise", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody CountryWiseCases getNewCasesCountryWise(
            @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader)
            throws IOException, ServletException {
        return impactAnalysisService.getNewCasesCountrywise(impactAnalysisService.maxDateForWhichDataIsAvailable());
    }

    @ApiOperation(value = "Returns newly reported cases today in single country passed in request parameter. today = current machine time", authorizations = {
            @Authorization(value = "Bearer") })
    @RequestMapping(value = "/api/data/newcases/country", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody CasesInfo getNewCasesForCountry(@RequestParam(value = "country") String country,
            @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader)
            throws IOException, ServletException {
        return impactAnalysisService.getNewCasesReportedInCountry(country);
    }

    @ApiOperation(value = "Returns TOP N countries with highest reported cases today. Today = current machine time", authorizations = {
            @Authorization(value = "Bearer") })
    @RequestMapping(value = "/api/data/newcases/topcountries", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody CountryWiseCases getTopNCountriesWithNewCasesToday(@RequestParam(value = "count") int count,
            @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader)
            throws IOException, ServletException {
        return impactAnalysisService.getTopNCountriesWithNewCases(count);
    }

    @ApiOperation(value = "Returns new cases reported each day since requested date in requested country.", authorizations = {
            @Authorization(value = "Bearer") })
    @RequestMapping(value = "/api/data/newcases/sincedate", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody DatewiseCountryData getTopNCountriesWithNewCasesSinceToday(
            @RequestParam(value = "date", required = true) @DateTimeFormat(pattern = "MM/dd/yy") Date date,
            @RequestParam(value = "country") String country,
            @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader)
            throws IOException, ServletException {
        return impactAnalysisService.getCasesDataInCountrySinceDate(country, date);
    }

}
