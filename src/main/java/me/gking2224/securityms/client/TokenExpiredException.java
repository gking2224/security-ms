package me.gking2224.securityms.client;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class TokenExpiredException extends SessionAuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = -2846698377332102062L;

    public TokenExpiredException(String msg) {
        super(msg);
    }

}
