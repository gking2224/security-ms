package me.gking2224.securityms.client;

import java.io.Serializable;

public class KeepTokenAliveEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5551259137574381803L;
    
    private String token;

    public KeepTokenAliveEvent() {
        super();
    }

    public KeepTokenAliveEvent(String token) {
        this();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        KeepTokenAliveEvent other = (KeepTokenAliveEvent) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("KeepTokenAliveEvent [token=%s]", token);
    }
}
