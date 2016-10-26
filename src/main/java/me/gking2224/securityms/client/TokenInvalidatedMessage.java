package me.gking2224.securityms.client;

import org.springframework.security.core.AuthenticationException;

public class TokenInvalidatedMessage implements SecurityEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 5551259137574381803L;
    
    private String token;

    private AuthenticationException exception;

    public TokenInvalidatedMessage() {
        super();
    }

    public TokenInvalidatedMessage(String token, AuthenticationException exception) {
        this();
        this.token = token;
        this.exception = exception;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationException getException() {
        return exception;
    }

    public void setException(AuthenticationException exception) {
        this.exception = exception;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TokenInvalidatedMessage other = (TokenInvalidatedMessage) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("TokenInvalidatedMessage [token=%s]", token);
    }
}
