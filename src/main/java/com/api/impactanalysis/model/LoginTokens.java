package com.api.impactanalysis.model;

public class LoginTokens {
	private String token;
	private String refreshToken;

	public LoginTokens(String token, String refreshToken) {
		this.token = token;
		this.refreshToken = refreshToken;
	}

	public LoginTokens() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
