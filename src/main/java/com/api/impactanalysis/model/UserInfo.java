package com.api.impactanalysis.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

public class UserInfo {
	private final String username;
	private final List<GrantedAuthority> authorities;

	private UserInfo(String username, List<GrantedAuthority> authorities) {
		this.username = username;
		this.authorities = authorities;
	}

	public static UserInfo create(String username, List<GrantedAuthority> authorities) {
		if (!StringUtils.hasText(username))
			throw new IllegalArgumentException("Username is blank: " + username);
		return new UserInfo(username, authorities);
	}

	public String getUsername() {
		return username;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
