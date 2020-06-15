package com.api.impactanalysis.security.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.api.impactanalysis.model.AccessJwtToken;
import com.api.impactanalysis.model.UserInfo;

public class JwtAuthenticationTokenImpl extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 5926650961304046873L;
	private AccessJwtToken rawAccessToken;
	private UserInfo userInfo;

	public JwtAuthenticationTokenImpl(AccessJwtToken unsafeToken) {
		super(null);
		this.rawAccessToken = unsafeToken;
		this.setAuthenticated(false);
	}

	public JwtAuthenticationTokenImpl(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.eraseCredentials();
		this.userInfo = userInfo;
		super.setAuthenticated(true);
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		if (authenticated) {
			throw new IllegalArgumentException("Setter based authentication setup not allowed. Use constructor to set property.");
		}
		super.setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return rawAccessToken;
	}

	@Override
	public Object getPrincipal() {
		return this.userInfo;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		this.rawAccessToken = null;
	}
}
