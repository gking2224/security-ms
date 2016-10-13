package me.gking2224.securityms.client;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContext;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.web.View;


public class Authentication implements org.springframework.security.core.Authentication, SecurityContext {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8023535085379115342L;

    private static final String PERMISSION_PREFIX = "Permission:";
    private static final String ROLE_PREFIX = "ROLE_";
    
    private UserDetails user;
    private Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
    private Set<String> roles = new HashSet<String>();
    private Set<String> permissions = new HashSet<String>();
    private Long expiry;
    private boolean isAuthenticated = false;

    public Authentication() {
        super();
    }
    
    public Authentication(
            final UserDetails user,
            final Set<String> roles,
            final Set<String> permissions,
            final Instant expiry
    ) {
        this.user = user;
        setRoles(roles);
        setPermissions(permissions);
        setExpiry(expiry.toEpochMilli());
        this.isAuthenticated = true;
    }
    
    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    @JsonView(View.Summary.class)
    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        clearPermissions();
        this.permissions = Collections.unmodifiableSet(permissions);
        this.authorities.addAll(permissions.stream()
                .map(p -> new GrantedAuthority(PERMISSION_PREFIX+p))
                .collect(Collectors.toSet()));
    }

    private void clearPermissions() { clearBy(PERMISSION_PREFIX); }
    private void clearRoles() { clearBy(ROLE_PREFIX); }
    private void clearBy(final String prefix) {
        this.authorities = this.authorities.stream()
                .filter(a -> !a.getAuthority().startsWith(prefix))
                .collect(Collectors.toSet());
    }

    @JsonView(View.Summary.class)
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        clearRoles();
        this.roles = Collections.unmodifiableSet(roles);
        this.authorities.addAll(roles.stream()
                .map(r -> new GrantedAuthority(ROLE_PREFIX+r))
                .collect(Collectors.toSet()));
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    @JsonView(View.Summary.class)
    public Object getPrincipal() {
        return user;
    }
    
    public void setPrincipal(final UserDetails principal) {
        this.user = principal;
    }

    @Override
    @JsonView(View.Summary.class)
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(final boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;;
    }

    @Override
    public String toString() {
        return String.format(
                "Authentication [user=%s, roles=%s, permissions=%s, isAuthenticated=%s]",
                user, roles, permissions, isAuthenticated);
    }
    
    public static class GrantedAuthority implements org.springframework.security.core.GrantedAuthority {

        /**
         * 
         */
        private static final long serialVersionUID = 1153713318378503725L;

        private String authority;

        public GrantedAuthority() {
            super();
        }

        public GrantedAuthority(final String authority) {
            this.authority = authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        @JsonView(View.Summary.class)
        public String getAuthority() {
            return authority;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((authority == null) ? 0 : authority.hashCode());
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
            GrantedAuthority other = (GrantedAuthority) obj;
            if (authority == null) {
                if (other.authority != null)
                    return false;
            } else if (!authority.equals(other.authority))
                return false;
            return true;
        }

        
        @Override
        public String toString() {
            return String.format("[%s]", authority);
        }
    }

    @Override
    public org.springframework.security.core.Authentication getAuthentication() {
        return this;
    }

    @Override
    public void setAuthentication(org.springframework.security.core.Authentication authentication) {
        throw new UnsupportedOperationException();
    }

}
