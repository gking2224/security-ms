package me.gking2224.securityms.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.web.ResponseErrorWriter;
import me.gking2224.securityms.client.InvalidTokenException;
import me.gking2224.securityms.client.ServiceUnavailableException;
import me.gking2224.securityms.client.TokenExpiredException;

@ControllerAdvice
@Profile("web")
public class SecurityErrorHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(SecurityErrorHandler.class);
    
    @Autowired
    private ResponseErrorWriter errorWriter;
    
    public SecurityErrorHandler() {
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> cannotAuthenticate() {
        return responseEntity(HttpStatus.FORBIDDEN, "Cannot authenticate");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied() {
        return responseEntity(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> serviceUnavailable() {
        return responseEntity(HttpStatus.SERVICE_UNAVAILABLE, "Security service unavailable");
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> internalErrr() {
        return responseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal authentication error");
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> tokenExpired() {
        return responseEntity(HttpStatus.UNAUTHORIZED, "Token expired");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidToken() {
        return responseEntity(HttpStatus.BAD_REQUEST, "Invalid token");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> genericError() {
        return responseEntity(HttpStatus.FORBIDDEN, "Authentication error");
    }

    private ResponseEntity<ErrorResponse> responseEntity(final HttpStatus status, final String description) {
        return new ResponseEntity<ErrorResponse>(errorResponse(status, description), status);
    }

    private ErrorResponse errorResponse(final HttpStatus status, final String description) {
        return new ErrorResponse(status.value(), description);
    }
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        errorWriter.writeError(request, response, errorResponse(HttpStatus.FORBIDDEN, "Access denied"));
    }
}
