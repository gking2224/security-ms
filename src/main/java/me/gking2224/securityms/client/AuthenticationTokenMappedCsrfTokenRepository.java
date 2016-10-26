package me.gking2224.securityms.client;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationTokenMappedCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    
    private String headerName = DEFAULT_CSRF_HEADER_NAME;
    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;
    
    @Autowired @Qualifier("csrfTokenStore")
    private Store<String, CsrfToken> store;

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(this.headerName, this.parameterName, createToken());
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String securityToken = getAuthenticationToken(request);
        if (securityToken != null) {
            store.store(securityToken, token);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        String authenticationToken = getAuthenticationToken(request);
        if (authenticationToken != null) {
            return getMappedToken(authenticationToken);
        }
        else {
            return null;
        }
    }

    private String getAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(TokenProcessingFilter.TOKEN_HEADER);
        return token;
    }

    @Cacheable("csrfTokens")
    private CsrfToken getMappedToken(String token) {
        return store.load(token);
    }
}
