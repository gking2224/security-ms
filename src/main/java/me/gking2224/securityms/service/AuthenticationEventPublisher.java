package me.gking2224.securityms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventPublisher
        implements org.springframework.security.authentication.AuthenticationEventPublisher {

    private Logger logger = LoggerFactory.getLogger(AuthenticationEventPublisher.class);
    
    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        logger.debug("auth success: {}", authentication);
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        logger.info("auth failure: {}", authentication);
    }

}
