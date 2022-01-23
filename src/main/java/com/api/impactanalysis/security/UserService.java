package com.api.impactanalysis.security;

import java.util.Optional;

import com.api.impactanalysis.entity.User;

public interface UserService {
    public Optional<User> getByUsername(String username);
}
