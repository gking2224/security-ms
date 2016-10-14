package me.gking2224.securityms.web.mvc;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private AuthenticationService authService;

    @RequestMapping(value="/authenticate", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    @JsonView(View.Summary.class)
    public ResponseEntity<Authentication> createToken(@RequestBody TokenRequest tokenRequest) {

        Authentication auth = authService.authenticate(tokenRequest.getUsername(), tokenRequest.getPassword());
        
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Authentication>(auth, headers, HttpStatus.OK);
    }

    @RequestMapping(value="/validate", method=RequestMethod.GET)
    @JsonView(View.Summary.class)
    public ResponseEntity<Authentication> validate(
            @RequestParam String securityToken
    ) {
        
        Authentication auth = authService.validate(securityToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setExpires(auth.getExpiry());
        headers.setCacheControl(String.format("max-age=%d", (auth.getExpiry() - Instant.now().toEpochMilli()) / 1000));
        return new ResponseEntity<Authentication>(auth, headers, HttpStatus.OK);
    }
}
