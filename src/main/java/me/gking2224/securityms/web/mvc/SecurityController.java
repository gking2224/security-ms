package me.gking2224.securityms.web.mvc;

import static java.lang.String.format;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.web.View;
import me.gking2224.securityms.client.Authentication;
import me.gking2224.securityms.client.TokenRequest;
import me.gking2224.securityms.service.AuthenticationService;

@RestController
public class SecurityController {

    private static final long DEFAULT_MAX_CACHE_PERIOD = 5;

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private AuthenticationService authService;

    private long maximumCachePeriodMinutes = DEFAULT_MAX_CACHE_PERIOD;

    @RequestMapping(value="/authenticate", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    @JsonView(View.Summary.class)
    public ResponseEntity<Authentication> createToken(@RequestBody TokenRequest tokenRequest) {

        Authentication auth = authService.authenticate(tokenRequest.getUsername(), tokenRequest.getPassword());
        
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Authentication>(auth, headers, HttpStatus.OK);
    }

    @RequestMapping(value="/validate/{token}", method=RequestMethod.GET)
    @JsonView(View.Summary.class)
    public ResponseEntity<Authentication> validate(@PathVariable("token") String securityToken) {
        
        Authentication auth = authService.validate(securityToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setExpires(auth.getExpiry());
        headers.setCacheControl(format("max-age=%d", getMaxAgeHeaderSecs(auth)));
        return new ResponseEntity<Authentication>(auth, headers, HttpStatus.OK);
    }

    @RequestMapping(value="/invalidate/{token}", method=RequestMethod.PUT)
    public ResponseEntity<Void> invalidate(@PathVariable("token") String securityToken) {
        
        authService.invalidate(securityToken);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private int getMaxAgeHeaderSecs(final Authentication auth) {
        
        long intervalMillis = auth.getExpiry() - Instant.now().toEpochMilli();
        
        int intervalSecs = (int)intervalMillis / 1000;
        int maxCacheSecs = (int)maximumCachePeriodMinutes * 60;
        
        return Math.min(maxCacheSecs, intervalSecs);
    }

    @RequestMapping(value="/validate", method=RequestMethod.OPTIONS)
    public ResponseEntity<Void> validatePreFlight(
            @RequestParam String securityToken
    ) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.OK);
    }
}
