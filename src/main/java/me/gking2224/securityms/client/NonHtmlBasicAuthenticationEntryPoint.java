package me.gking2224.securityms.client;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.web.ResponseErrorWriter;

@Component
@PropertySource("/security.properties")
public class NonHtmlBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Value("${realmName}")
    private String realmName;
    
    @Autowired
    ResponseErrorWriter errorWriter;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException e
    ) throws IOException, ServletException {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, String.format("Basic realm=\"%s\"", realmName));
        errorWriter.writeError(request, response, new ErrorResponse(SC_UNAUTHORIZED, e.getMessage()));
    }
}
