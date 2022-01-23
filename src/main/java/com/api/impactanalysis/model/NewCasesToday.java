package com.api.impactanalysis.model;

import java.io.Serializable;

public class NewCasesToday implements Serializable {
    private static final long serialVersionUID = 937349069678201116L;
    private Long globallyReportedNewCases;
    private String description;

    public NewCasesToday() {
    }

    public NewCasesToday(Long globallyReportedNewCases, String description) {
        this.globallyReportedNewCases = globallyReportedNewCases;
        this.description = description;
    }

    public Long getGloballyReportedNewCases() {
        return globallyReportedNewCases;
    }

    public void setGloballyReportedNewCases(Long globallyReportedNewCases) {
        this.globallyReportedNewCases = globallyReportedNewCases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
