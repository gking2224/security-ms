package me.gking2224.securityms.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class DefaultToken implements Token<DefaultToken> {

    private String username;
    private Set<Long> permissions;
    private Instant timeout;

    @Override
    public DefaultToken username(final String username) {
        if (this.username != null) accessError();
        this.username = username;
        return this;
    }

    @Override
    public DefaultToken timeout(Instant timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public DefaultToken permissions(Set<Long> permissions) {
        this.permissions = Collections.unmodifiableSet(permissions);
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Set<Long> getPermissions() {
        return permissions;
    }

    @Override
    public Instant getTimeout() {
        return timeout;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(username);
        out.writeLong(timeout.toEpochMilli());
        out.writeInt(permissions.size());
        for (Long p : permissions)
            out.writeLong(p);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.username = (String)in.readObject();
        this.timeout = Instant.ofEpochMilli(in.readLong());
        int remaining = in.readInt();
        Set<Long> permissions = new HashSet<Long>();
        while (remaining > 0) {
            permissions.add(in.readLong());
            remaining--;
        }
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return String.format("Token [username=%s, permissions=%s, timeout=%s]", username, permissions, timeout);
    }

    private static void accessError() {
        throw new InternalAuthenticationServiceException("Illegal attempt to modify token");
    }


}
