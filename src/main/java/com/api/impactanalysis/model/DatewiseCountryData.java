package com.api.impactanalysis.model;

import java.io.Serializable;
import java.util.List;

public class DatewiseCountryData implements Serializable {
    private static final long serialVersionUID = -4220801207469962875L;
    private List<CasesInfo> dateWiseNewCases;
    private String country;
    private String referenceDate;
    private String description;

    public List<CasesInfo> getDateWiseNewCases() {
        return dateWiseNewCases;
    }

    public void setDateWiseNewCases(List<CasesInfo> dateWiseNewCases) {
        this.dateWiseNewCases = dateWiseNewCases;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
