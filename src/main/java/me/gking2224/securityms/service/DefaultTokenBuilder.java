package me.gking2224.securityms.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import me.gking2224.securityms.model.DefaultToken;
import me.gking2224.securityms.model.Token;

public class DefaultTokenBuilder implements TokenBuilder {

    private String username;
    private Instant timeout;
    private Set<Long> permissions = Collections.emptySet();

    public Token<?> buildToken() {
        
        return new DefaultToken().username(username).timeout(timeout).permissions(permissions);
    }
    
    public DefaultTokenBuilder forUser(final String username) {
        
        this.username = username;
        return this;
    }

    public DefaultTokenBuilder withExpiry(Instant timeout) {
        this.timeout = timeout;
        return this;
    }

    public DefaultTokenBuilder withPermissions(Set<Long> permissions) {
        this.permissions = permissions;
        return this;
    }
}
