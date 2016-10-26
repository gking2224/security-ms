package me.gking2224.securityms.client;

import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

@Component
public class CookieCsrfTokenRepository implements CsrfTokenRepository {
    
    @SuppressWarnings("unused")
    private static Logger LOG = LoggerFactory.getLogger(CookieCsrfTokenRepository.class);

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
        response.addCookie(new Cookie(this.headerName, token.getToken()));
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            cookie = Arrays.asList(cookies)
                    .stream()
                    .filter(c -> headerName.equals(c.getName()))
                    .findFirst()
                    .get();
        }
        
        DefaultCsrfToken token = null;
        if (cookie != null) {
            token = new DefaultCsrfToken(this.headerName, this.parameterName, cookie.getValue());
        }
        return token;
    }
}
