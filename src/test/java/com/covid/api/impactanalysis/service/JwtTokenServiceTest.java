package com.covid.api.impactanalysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.api.impactanalysis.model.AccessJwtToken;
import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.security.config.Configurations;
import com.api.impactanalysis.security.exceptions.ExpiredJWTException;
import com.api.impactanalysis.service.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

class JwtTokenServiceTest {
	private JwtTokenService jwtTokenService;
	@Mock
	private Configurations configurations;
	@Mock
	List<GrantedAuthority> grantedAuthorities;

	@BeforeEach
	private void doSetup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(configurations.getRefreshTokenExpTime()).thenReturn(1);
		Mockito.when(configurations.getTokenExpirationTime()).thenReturn(1);
		Mockito.when(configurations.getTokenIssuer()).thenReturn("IssuerAuthority");
		Mockito.when(configurations.getSecretKeySignatureAlgo()).thenReturn("HS256");
		grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
		grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
		jwtTokenService = new JwtTokenService(configurations);
	}

	@Test
	void testCreateAccessJwtToken() {
		UserInfo userInfo = UserInfo.create("user1", grantedAuthorities);
		String accessToken = jwtTokenService.createAccessJwtToken(userInfo).getToken();
		assertEquals(true, accessToken.length() > 0);
	}

	@Test
	void testCreateRefreshToken() {
		UserInfo userInfo = UserInfo.create("user1", grantedAuthorities);
		String refreshToken = jwtTokenService.createRefreshToken(userInfo).getToken();
		assertEquals(true, refreshToken.length() > 0);
	}

	@Test
	void testGetSecretKey() {
		assertEquals(true, jwtTokenService.getSecretKey() instanceof SecretKey);
	}

	@Test
	void testGetAllAuthenticatedUsers() {
		UserInfo userInfo1 = UserInfo.create("user1", grantedAuthorities);
		UserInfo userInfo2 = UserInfo.create("user2", grantedAuthorities);
		jwtTokenService.createAccessJwtToken(userInfo1);
		jwtTokenService.createAccessJwtToken(userInfo2);
		List<String> expectedUsers = Arrays.asList("user1", "user2");
		List<String> actualUsersWithTokens = new ArrayList<String>(jwtTokenService.getAllAuthenticatedUsers());
		assertEquals(expectedUsers, actualUsersWithTokens);
	}

	@Test
	void testValidateIfTokenIsExplicitlyInvalidated() {
		UserInfo userInfo1 = UserInfo.create("user1", grantedAuthorities);
		String token = jwtTokenService.createAccessJwtToken(userInfo1).getToken();
		jwtTokenService.validateIfTokenIsExplicitlyInvalidated(token, "user1");
	}

	@Test
	@DisplayName("Test if user created a token and then re-generated a token again then for his previous token exception is thrown")
	void testIfUserHasNewTokenGeneratedThenExceptionIsThrown() throws InterruptedException {
		UserInfo userInfo1 = UserInfo.create("user1", grantedAuthorities);
		String token1 = jwtTokenService.createAccessJwtToken(userInfo1).getToken();
		TimeUnit.MILLISECONDS.sleep(2000);
		jwtTokenService.createAccessJwtToken(userInfo1).getToken();
		assertThrows(BadCredentialsException.class, () -> jwtTokenService.validateIfTokenIsExplicitlyInvalidated(token1, "user1"));
	}

	@Test
	void testParseClaims() {
		UserInfo userInfo1 = UserInfo.create("user1", grantedAuthorities);
		String token1 = jwtTokenService.createAccessJwtToken(userInfo1).getToken();
		Jws<Claims> parseClaims = jwtTokenService.parseClaims(new AccessJwtToken(token1));
		assertEquals("user1", parseClaims.getBody().getSubject());
	}

	@Test
	@DisplayName("Test that refresh token is verified with success.")
	void testVerifyRefreshToken() throws Exception {
		UserInfo userInfo = UserInfo.create("user1", grantedAuthorities);
		String refreshToken = jwtTokenService.createRefreshToken(userInfo).getToken();
		String subject = jwtTokenService.verifyRefreshToken(new AccessJwtToken(refreshToken)).orElseThrow(()->new Exception("Failed due to invalid token.")).getBody().getSubject();
		assertEquals("user1", subject);
	}

	
	@Test
	@DisplayName("Test empty optional is returned if token is not of refresh type")
	void testEmptyOptionalIfTokenIsNotOfRefreshType() {
		UserInfo userInfo = UserInfo.create("user1", grantedAuthorities);
		String acccessToken = jwtTokenService.createAccessJwtToken(userInfo).getToken();
		assertEquals(false, jwtTokenService.verifyRefreshToken(new AccessJwtToken(acccessToken)).isPresent());
	}
	
	@Test
	@DisplayName("Test that when token is expired. Then upon parsing expired token ExpiredJWTException is thrown")
	void testForExpiredTokenExceptionIsThrown() throws InterruptedException {
		UserInfo userInfo1 = UserInfo.create("user1", grantedAuthorities);
		String token1 = jwtTokenService.createAccessJwtToken(userInfo1).getToken();
		TimeUnit.MINUTES.sleep(configurations.getTokenExpirationTime());
		assertThrows(ExpiredJWTException.class, ()->jwtTokenService.parseClaims(new AccessJwtToken(token1)));
	}

}
