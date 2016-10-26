package me.gking2224.securityms.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

@Component("csrfHeaderWritingFilter")
public class CsrfHeaderWritingFilter extends OncePerRequestFilter {

    private static final String GET_CSRF_PATH = "/__get_csrf_token";
    
    @SuppressWarnings("unused")
    private static Logger LOG = LoggerFactory.getLogger(CsrfHeaderWritingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        String path = new UrlPathHelper().getPathWithinApplication(request);

        if (request.getMethod().equals(RequestMethod.GET.name()) && GET_CSRF_PATH.equals(path)) {
            if (token != null) {
                response.getWriter().write(token.getToken());
                return;
            }
            else {
                // possible?
                throw new MissingCsrfTokenException(null);
            }
        }
        filterChain.doFilter(request, response);
//        Enumeration<String> headers = request.getHeaderNames();
//        while (headers.hasMoreElements()) {
//            String header = headers.nextElement();
//            LOG.debug("{}: {}", header, request.getHeader(header)); 
//        }
//        filterChain.doFilter(request, response);
//        
//        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
//
//        // Spring Security will allow the Token to be included in this header name
//        response.setHeader("X-CSRF-HEADER", token.getHeaderName());
//
//        // Spring Security will allow the token to be included in this parameter name
//        response.setHeader("X-CSRF-PARAM", token.getParameterName());
//
//        // this is the value of the token to be included as either a header or an HTTP parameter
//        response.setHeader("X-CSRF-TOKEN", token.getToken());
        
    }

}
