package com.api.impactanalysis.security.auth.matchers.extractor;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtHeaderTokenExtractorImpl implements HeaderTokenExtractor {
	public static final String HEADER_PREFIX = "Bearer ";

	@Override
	public String extract(String header) {
		if (!StringUtils.hasText(header)) {
			throw new AuthenticationServiceException("Authorization header cannot be blank!");
		}
		if (header.length() < HEADER_PREFIX.length()) {
			throw new AuthenticationServiceException("Invalid authorization header size.");
		}
		return header.startsWith(HEADER_PREFIX)? header.substring(HEADER_PREFIX.length(), header.length()).trim(): header.trim(); 
	}
}
