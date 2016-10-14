package me.gking2224.securityms.service;

import java.time.Instant;

import me.gking2224.securityms.model.DefaultToken;
import me.gking2224.securityms.model.Token;

public class DefaultTokenBuilder implements TokenBuilder {

    private String username;
    private Instant timeout;

    public Token<?> buildToken() {
        
        return new DefaultToken().username(username).timeout(timeout);
    }
    
    public DefaultTokenBuilder forUser(final String username) {
        
        this.username = username;
        return this;
    }

    public DefaultTokenBuilder withExpiry(Instant timeout) {
        this.timeout = timeout;
        return this;
    }
}
