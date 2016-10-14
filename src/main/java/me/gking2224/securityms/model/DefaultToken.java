package me.gking2224.securityms.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Instant;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class DefaultToken implements Token<DefaultToken> {

    private String username;
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
    public String getUsername() {
        return username;
    }

    @Override
    public Instant getTimeout() {
        return timeout;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(username);
        out.writeLong(timeout.toEpochMilli());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.username = (String)in.readObject();
        this.timeout = Instant.ofEpochMilli(in.readLong());
    }

    @Override
    public String toString() {
        return String.format("Token [username=%s, timeout=%s]", username, timeout);
    }

    private static void accessError() {
        throw new InternalAuthenticationServiceException("Illegal attempt to modify token");
    }


}
