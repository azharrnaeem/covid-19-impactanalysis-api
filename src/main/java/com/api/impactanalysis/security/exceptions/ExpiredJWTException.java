package com.api.impactanalysis.security.exceptions;

import org.springframework.security.core.AuthenticationException;

import com.api.impactanalysis.model.JwtToken;

public class ExpiredJWTException extends AuthenticationException {
    private static final long serialVersionUID = -3017024183369126055L;
    private JwtToken token;

    public ExpiredJWTException(String msg) {
        super(msg);
    }

    public ExpiredJWTException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
