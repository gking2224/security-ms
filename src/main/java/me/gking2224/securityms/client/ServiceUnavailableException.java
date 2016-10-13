package me.gking2224.securityms.client;

import org.springframework.security.authentication.AuthenticationServiceException;

public class ServiceUnavailableException extends AuthenticationServiceException {

    /**
     * 
     */
    private static final long serialVersionUID = 3462321916837095423L;

    public ServiceUnavailableException(String msg, Throwable t) {
        super(msg, t);
    }

}
