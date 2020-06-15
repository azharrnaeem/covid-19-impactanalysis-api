package com.api.impactanalysis.model;

import java.io.Serializable;

public class CasesInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String countryName;
	private Long cases;
	private String date;

	public CasesInfo() {
	}

	public CasesInfo(String countryName, Long cases) {
		this.countryName = countryName;
		this.cases = cases;
	}

	public CasesInfo(String countryName, Long cases, String date) {
		this.countryName = countryName;
		this.cases = cases;
		this.date = date;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Long getCases() {
		return cases;
	}

	public void setCases(Long cases) {
		this.cases = cases;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
