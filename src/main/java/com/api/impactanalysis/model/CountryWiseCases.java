package com.api.impactanalysis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CountryWiseCases implements Serializable {
    private static final long serialVersionUID = -8737730500019742459L;
    private List<CasesInfo> countryWiseCasesData;
    private Date date;
    private String description;

    public List<CasesInfo> getCountryWiseCasesData() {
        return countryWiseCasesData;
    }

    public void setCountryWiseCasesData(List<CasesInfo> countryWiseCasesData) {
        this.countryWiseCasesData = countryWiseCasesData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
