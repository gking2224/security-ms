package me.gking2224.securityms.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.client.ErrorResponseException;

@Component
public class TokenProcessingFilter extends OncePerRequestFilter {
    
    private static final String TOKEN_HEADER = "Authentication";

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(TokenProcessingFilter.class);
    
    @Autowired(required=true)
    private SecurityServiceClient serviceClient;
    
//    @Autowired
//    private SecurityErrorHandler errorHandler;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader(TOKEN_HEADER);
        if (token != null) {
            validate(token);
        }
        chain.doFilter(request, response);
    }

    private void validate(String token) {
        try {
            Authentication auth = serviceClient.validate(token);
            storeAuthentication(auth);
        }
        catch (ServiceUnavailableException e) {
            throw new ErrorResponseException(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Security Service not available"));
        }
    }

    private void storeAuthentication(Authentication auth) {
        SecurityContextHolder.setContext(auth);
    }

    @Override
    public void destroy() {}

}
