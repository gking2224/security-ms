package me.gking2224.securityms.service;

import java.time.Instant;

import me.gking2224.securityms.model.Token;

public interface TokenBuilder {

    Token<?> buildToken();

    TokenBuilder forUser(String username);

    TokenBuilder withExpiry(Instant timeout);
}
