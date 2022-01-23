package com.api.impactanalysis.security.auth.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.api.impactanalysis.common.Constants;
import com.api.impactanalysis.model.AccessJwtToken;
import com.api.impactanalysis.model.JwtToken;
import com.api.impactanalysis.model.UserInfo;
import com.api.impactanalysis.security.auth.JwtAuthenticationTokenImpl;
import com.api.impactanalysis.service.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtAuthenticationProvider(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtToken rawAccessToken = (AccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = jwtTokenService.parseClaims(rawAccessToken);
        String subject = jwsClaims.getBody().getSubject();
        jwtTokenService.validateIfTokenIsExplicitlyInvalidated(rawAccessToken.getToken(), subject);
        List<String> scopes = jwsClaims.getBody().get(Constants.SCOPES, List.class);
        List<GrantedAuthority> authorities = scopes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UserInfo userInfo = UserInfo.create(subject, authorities);
        return new JwtAuthenticationTokenImpl(userInfo, userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationTokenImpl.class.isAssignableFrom(authentication));
    }
}
