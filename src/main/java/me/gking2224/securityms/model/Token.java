package me.gking2224.securityms.model;

import java.io.Externalizable;
import java.time.Instant;

public interface Token<T> extends Externalizable {

    public T username(final String username);

    T timeout(Instant timeout);

    String getUsername();

    Instant getTimeout();
}
