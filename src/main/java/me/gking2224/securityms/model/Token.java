package me.gking2224.securityms.model;

import java.io.Externalizable;
import java.time.Instant;
import java.util.Set;

public interface Token<T> extends Externalizable {

    public T username(final String username);

    T timeout(Instant timeout);

    T permissions(Set<Long> permissions);

    String getUsername();

    Set<Long> getPermissions();

    Instant getTimeout();
}
