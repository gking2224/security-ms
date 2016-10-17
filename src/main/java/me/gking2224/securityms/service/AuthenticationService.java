package me.gking2224.securityms.service;

import org.springframework.security.core.AuthenticationException;

import me.gking2224.securityms.client.Authentication;

public interface AuthenticationService {

    Authentication authenticate(String username, String password) throws AuthenticationException;

    Authentication validate(String key) throws AuthenticationException;

    void invalidate(String securityToken);
    
    void keepTokenAlive(String token) throws AuthenticationException;

}
