package com.api.impactanalysis.v1.conrtollers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.impactanalysis.entity.User;
import com.api.impactanalysis.model.AccessJwtToken;
import com.api.impactanalysis.model.JwtToken;
import com.api.impactanalysis.model.LoginTokens;
import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.security.UserService;
import com.api.impactanalysis.security.auth.matchers.extractor.HeaderTokenExtractor;
import com.api.impactanalysis.security.exceptions.InvalidJwtToken;
import com.api.impactanalysis.security.filters.LoginRequest;
import com.api.impactanalysis.service.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class TokenEndpointsController {
	@Autowired
	private JwtTokenService jwtTokenService;
	@Autowired
	private UserService userService;
	@Autowired
	private HeaderTokenExtractor tokenExtractor;

	@ApiOperation(value = "Generates new access token and accepts refresh token return at time of login", authorizations = {@Authorization(value = "Bearer")})
	@RequestMapping(value = "/api/auth/token", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(@RequestHeader(value = "Authorization", defaultValue = "", required = true) String authorizationHeader) throws IOException, ServletException {
		String tokenPayload = tokenExtractor.extract(authorizationHeader);
		JwtToken rawToken = new AccessJwtToken(tokenPayload);
		Jws<Claims> orElseThrow = jwtTokenService.verifyRefreshToken(rawToken).orElseThrow(() -> new InvalidJwtToken("Invalid Refresh Token."));
		String subject = orElseThrow.getBody().getSubject();
		User user = userService.getByUsername(subject).orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));
		if (user.getRoles() == null) {
			throw new InsufficientAuthenticationException("User has no roles assigned");
		}
		List<GrantedAuthority> authorities = user.getRoles().stream().map(authority -> new SimpleGrantedAuthority(authority.getRole().authority())).collect(Collectors.toList());
		UserInfo userInfo = UserInfo.create(user.getUsername(), authorities);
		return jwtTokenService.createAccessJwtToken(userInfo);
	}

	@ApiOperation(value = "Login with credentials i.e username, password to get token.", authorizations = {@Authorization(value = "Bearer")})
	@RequestMapping(value = "/api/auth/login", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public LoginTokens login(@RequestBody(required = true) LoginRequest loginRequest) throws IOException, ServletException {
		//left unimplemented as login is handled through filter and this is for swagger documentation only.
		return new LoginTokens();
	}
}
