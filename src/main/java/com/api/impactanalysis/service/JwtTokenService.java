package com.api.impactanalysis.service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.api.impactanalysis.common.CommonUtils;
import com.api.impactanalysis.common.Constants;
import com.api.impactanalysis.model.AccessJwtToken;
import com.api.impactanalysis.model.JwtToken;
import com.api.impactanalysis.model.Scopes;
import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.security.config.Configurations;
import com.api.impactanalysis.security.exceptions.ExpiredJWTException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenService.class);
	private final Configurations configurations;
	private final Key secretKey;
	private final Map<String, String> userToTokenMappings;

	@Autowired
	public JwtTokenService(Configurations configurations) {
		this.configurations = configurations;
		this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.forName(configurations.getSecretKeySignatureAlgo()));
		this.userToTokenMappings = new ConcurrentHashMap<>();
	}

	public JwtToken createAccessJwtToken(UserInfo userInfo) {
		if (!StringUtils.hasText(userInfo.getUsername())) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}

		if (userInfo.getAuthorities() == null || userInfo.getAuthorities().isEmpty()) {
			throw new IllegalArgumentException("User doesn't have any privileges");
		}
		Claims claims = Jwts.claims().setSubject(userInfo.getUsername());
		claims.put(Constants.SCOPES, userInfo.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		LocalDateTime currentTime = LocalDateTime.now();
		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuer(configurations.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(configurations.getTokenExpirationTime()).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(getSecretKey())
				.compact();
		userToTokenMappings.put(userInfo.getUsername(), token);
		return new AccessJwtToken(token);
	}

	public JwtToken createRefreshToken(UserInfo userInfo) {
		if (!StringUtils.hasText(userInfo.getUsername())) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}
		LocalDateTime currentTime = LocalDateTime.now();
		Claims claims = Jwts.claims().setSubject(userInfo.getUsername());
		claims.put(Constants.SCOPES, Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

		String token = Jwts.builder().setClaims(claims)
				.setIssuer(configurations.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(configurations.getRefreshTokenExpTime()).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(getSecretKey())
				.compact();
		return new AccessJwtToken(token);
	}

	public Key getSecretKey() {
		return secretKey;
	}

	public Collection<String> getAllAuthenticatedUsers() {
		return userToTokenMappings.keySet();
	}

	public void validateIfTokenIsExplicitlyInvalidated(String token, String subject) {
		String inMemoryToken = userToTokenMappings.get(subject);
		if (null == inMemoryToken || !inMemoryToken.equals(token)) {
			throw new BadCredentialsException("Token is invalided.");
		}
	}

	public Jws<Claims> parseClaims(JwtToken jwtToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken.getToken());
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SecurityException ex) {
			LOGGER.error("JWT token is not valid", ex);
			throw new BadCredentialsException("JWT token is not valid: ", ex);
		} catch (ExpiredJwtException expiredEx) {
			LOGGER.info("JWT token is expired", expiredEx);
			throw new ExpiredJWTException(jwtToken, "JWT token is expired", expiredEx);
		} catch (Exception e) {
			LOGGER.error("JWT token is not valid", e);
			throw new BadCredentialsException("JWT token is not valid: ", e);
		}
	}

	public Optional<Jws<Claims>> verifyRefreshToken(JwtToken token) {
		Jws<Claims> claims = parseClaims(token);
		List<String> scopes = CommonUtils.castList(String.class, claims.getBody().get(Constants.SCOPES, List.class));
		if (scopes == null || scopes.isEmpty() || !scopes.stream().filter(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)).findFirst().isPresent()) {
			return Optional.empty();
		}
		return Optional.of(claims);
	}
}
