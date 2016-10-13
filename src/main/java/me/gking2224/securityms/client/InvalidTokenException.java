package me.gking2224.securityms.client;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = 6139398277848335437L;

    public InvalidTokenException(String msg, Throwable t) {
        super(msg, t);
    }

}
