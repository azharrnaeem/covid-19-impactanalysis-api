package com.api.impactanalysis.security.auth.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.api.impactanalysis.model.JwtToken;
import com.api.impactanalysis.model.LoginTokens;
import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
	private final ObjectMapper mapper;
	private final JwtTokenService jwtTokenService;

	@Autowired
	public AuthenticationSuccessHandlerImpl(final ObjectMapper mapper, final JwtTokenService jwtTokenService) {
		this.mapper = mapper;
		this.jwtTokenService = jwtTokenService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();
		JwtToken accessToken = jwtTokenService.createAccessJwtToken(userInfo);
		JwtToken refreshToken = jwtTokenService.createRefreshToken(userInfo);
		LoginTokens loginTokens = new LoginTokens(accessToken.getToken(), refreshToken.getToken());
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getWriter(), loginTokens);
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
