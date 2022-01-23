package com.api.impactanalysis.model;

public class AccessJwtToken implements JwtToken {
    private String token;

    public AccessJwtToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
