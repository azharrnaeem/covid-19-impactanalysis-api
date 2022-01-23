package com.api.impactanalysis.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

public class InvalidJwtToken extends AuthenticationServiceException {
    private static final long serialVersionUID = -294671188037098603L;

    public InvalidJwtToken(String msg) {
        super(msg);
    }

}
