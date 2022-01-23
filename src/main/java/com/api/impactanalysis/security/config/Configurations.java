package com.api.impactanalysis.security.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.impactnalysis.settings")
public class Configurations {
    private Integer tokenExpirationTime;
    private String tokenIssuer;
    private Integer refreshTokenExpTime;
    private String secretKeySignatureAlgo;
    private URI covidDataSourceURL;
    private String dataRefreshInterval;

    public Integer getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Integer tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getSecretKeySignatureAlgo() {
        return secretKeySignatureAlgo;
    }

    public void setSecretKeySignatureAlgo(String secretKeySignatureAlgo) {
        this.secretKeySignatureAlgo = secretKeySignatureAlgo;
    }

    public URI getCovidDataSourceURL() {
        return covidDataSourceURL;
    }

    public void setCovidDataSourceURL(URI covidDataSourceURL) {
        this.covidDataSourceURL = covidDataSourceURL;
    }

    public String getDataRefreshInterval() {
        return dataRefreshInterval;
    }

    public void setDataRefreshInterval(String dataRefreshInterval) {
        this.dataRefreshInterval = dataRefreshInterval;
    }
}
