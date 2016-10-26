package me.gking2224.securityms.client;

import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.client.ResponseErrorWriter;

@Component
public class NonHtmlBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final String DEFAULT_SECURITY_REALM = "securityms";

    @Autowired
    ResponseErrorWriter errorWriter;
    
    @Autowired MicroServiceEnvironment env;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException e
    ) throws IOException, ServletException {
        String realmName = env.getProperty("security.securityService.realmName", DEFAULT_SECURITY_REALM);
        response.addHeader(WWW_AUTHENTICATE, String.format("Basic realm=\"%s\"", realmName));
        errorWriter.writeError(request, response, new ErrorResponse(UNAUTHORIZED, e.getMessage()));
    }
}
