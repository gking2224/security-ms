package me.gking2224.securityms.client;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class SecurityServiceClient {
    
    private String host;
    private int port;
    private String context;
    private String baseUrl;
    private String createTokenUrl;
    private String validateUrl;
    
    private RestTemplate restTemplate;

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SecurityServiceClient() {
        super();
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
    protected String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = String.format("http://%s:%d/%s", this.host, this.port, this.context);
        }
        return baseUrl;
    }

    protected String getCreateTokenUrl() {
        if (createTokenUrl == null) {
            createTokenUrl = String.format("%s/createToken", getBaseUrl());
        }
        return createTokenUrl;
    }

    protected String getValidateUrl(final String token) {
        if (validateUrl == null) {
            validateUrl = String.format("%s/validate", getBaseUrl());
        }
        return String.format("%s?securityToken=%s", validateUrl, token);
    }
    
    public String createToken(final String username, final String password) {
        TokenRequest rq = new TokenRequest(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<TokenRequest> entity = new HttpEntity<TokenRequest>(rq, headers);

        ResponseEntity<Token> token = getRestTemplate().exchange(getCreateTokenUrl(), POST, entity, Token.class);
        return (String)token.getBody().getToken();
    }
    public Authentication validate(final String token) {


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.TEXT_PLAIN);

        try {
            return getRestTemplate().getForObject(getValidateUrl(token), Authentication.class);
        }
        catch (ResourceAccessException t) {
            throw new ServiceUnavailableException(t.getMessage(), t);
        }
    }
}
