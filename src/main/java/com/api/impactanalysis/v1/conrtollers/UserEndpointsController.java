package com.api.impactanalysis.v1.conrtollers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.security.auth.JwtAuthenticationTokenImpl;
import com.api.impactanalysis.service.JwtTokenService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class UserEndpointsController {
	private final JwtTokenService jwtTokenFactory;

	@Autowired
	public UserEndpointsController(JwtTokenService jwtTokenFactory) {
		this.jwtTokenFactory = jwtTokenFactory;
	}

	@ApiOperation(value = "Return username against token", authorizations = {@Authorization(value = "Bearer")})
	@RequestMapping(value = "/api/users/me", method = RequestMethod.GET)
	public @ResponseBody UserInfo getCurrentUser(JwtAuthenticationTokenImpl token, @RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader) {
		return (UserInfo) token.getPrincipal();
	}

	@ApiOperation(value = "Returns all users who have genrated a token and is still valid.", authorizations = {@Authorization(value = "Bearer")})
	@RequestMapping(value = "/api/users/allauthenticated", method = RequestMethod.GET)
	public @ResponseBody Map<String, Collection<String>> getAllAutenticatedUsers(@RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader) {
		Map<String, Collection<String>> mapper = new ConcurrentHashMap<>();
		mapper.put("authenticatedUsers", jwtTokenFactory.getAllAuthenticatedUsers());
		return mapper;
	}
}
