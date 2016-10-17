package me.gking2224.securityms.client;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class TokenInvalidatedException extends SessionAuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = -2846698377332102062L;

    public TokenInvalidatedException(String msg) {
        super(msg);
    }

}
