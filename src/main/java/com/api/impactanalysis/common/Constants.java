package com.api.impactanalysis.common;

import java.util.Arrays;
import java.util.List;

public class Constants {

	public static final String AUTHENTICATION_HEADER_KEY = "Authorization";
	public static final String AUTHENTICATION_END_POINT = "/api/auth/login";
	public static final String REFRESH_TOKEN_END_POINT = "/api/auth/token";
	public static final String API_ROOT_URL = "/api/**";
	public static final String CONSOLE_URL = "/console";
	public static final String SWAGGER_URL = "/swagger-resources/**";
	public static final String H2_CONSOLE = "/console";
	public static final List<String> PERMITTED_END_POINTS = Arrays.asList(AUTHENTICATION_END_POINT, REFRESH_TOKEN_END_POINT, H2_CONSOLE, SWAGGER_URL);
	public static final String SCOPES = "scopes";
	public static final String DATE_FORMAT_MM_DD_YY = "MM/dd/yy";
	public static final String DATE_FORMAT_M_D_YY = "M/dd/yy";
}
