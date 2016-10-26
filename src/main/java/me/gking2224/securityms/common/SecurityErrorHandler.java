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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.client.ResponseErrorWriter;
import me.gking2224.securityms.client.InvalidTokenException;
import me.gking2224.securityms.client.ServiceUnavailableException;
import me.gking2224.securityms.client.TokenExpiredException;
import me.gking2224.securityms.client.TokenInvalidatedException;

@ControllerAdvice
@Profile("web")
public class SecurityErrorHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(SecurityErrorHandler.class);
    
    @Autowired
    private ResponseErrorWriter errorWriter;
    
    public SecurityErrorHandler() {
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied() {
        return responseEntity(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> genericError(final AuthenticationException e) {
        return responseEntity(errorResponse(e));
    }
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException e) throws IOException, ServletException {
        errorWriter.writeError(request, response, errorResponse(e));
    }

    public ErrorResponse errorResponse(Throwable t) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = null;
        Class<? extends Throwable> clazz = t.getClass();
        if (BadCredentialsException.class.isAssignableFrom(clazz) ||
                UsernameNotFoundException.class.isAssignableFrom(clazz))
        {
            message = "Cannot authenticate";
        }
        else if (DisabledException.class.isAssignableFrom(clazz)) {
            message = "Unable to log on. Please contact your system administrator.";
        }
        else if (TokenInvalidatedException.class.isAssignableFrom(clazz)) {
            message = t.getMessage();
        }
        else if (InvalidTokenException.class.isAssignableFrom(clazz)) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid token";
        }
        else if (TokenExpiredException.class.isAssignableFrom(clazz)) {
            message = "Session expired";
        }
        else if (ServiceUnavailableException.class.isAssignableFrom(clazz)) {
            message = "Security service unavailable";
        }
        else if (MissingCsrfTokenException.class.isAssignableFrom(clazz) ||
                InvalidCsrfTokenException.class.isAssignableFrom(clazz))
        {
            message = "Access Denied";
        }
        else if (AuthenticationException.class.isAssignableFrom(clazz)) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authentication error";
        }
        else if (AccessDeniedException.class.isAssignableFrom(clazz)) {
            status = HttpStatus.FORBIDDEN;
            message = "Access denied";
        }
        
        return errorResponse(status, message);
    }
    
    private ResponseEntity<ErrorResponse> responseEntity(final HttpStatus status, final String description) {
        return new ResponseEntity<ErrorResponse>(errorResponse(status, description), status);
    }
    
    private ResponseEntity<ErrorResponse> responseEntity(final ErrorResponse errorResponse) {
        return new ResponseEntity<ErrorResponse>(errorResponse, errorResponse.getStatus());
    }
    
    private ErrorResponse errorResponse(final HttpStatus status, final String description) {
        return new ErrorResponse(status, description);
    }
}
