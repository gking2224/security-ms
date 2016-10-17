package me.gking2224.securityms.client;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Arrays;
import java.util.Collections;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class SecurityServiceClient {
    
    public static final String KEEP_TOKEN_ALIVE_TOPIC = "KeepTokenAlive";
    
    private String host;
    private int port;
    private String context;
    private String baseUrl;
    private String createTokenUrl;
    private String validateUrl;
    private String invalidateUrl;
    
    private RestTemplate restTemplate;
    
    private JmsTemplate jmsTemplate;
    private Topic keepTokenAliveTopic;

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

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Topic getKeepTokenAliveTopic() {
        return keepTokenAliveTopic;
    }

    public void setKeepTokenAliveTopic(Topic t) {
        this.keepTokenAliveTopic = t;
    }
    
    protected String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = String.format("http://%s:%d/%s", this.host, this.port, this.context);
        }
        return baseUrl;
    }

    protected String getAuthenticateUrl() {
        if (createTokenUrl == null) {
            createTokenUrl = String.format("%s/authenticate", getBaseUrl());
        }
        return createTokenUrl;
    }

    protected String getValidateUrl(final String token) {
        if (validateUrl == null) {
            validateUrl = String.format("%s/validate", getBaseUrl());
        }
        return String.format("%s/%s", validateUrl, token);
    }

    protected String getInvalidateUrl(final String token) {
        if (invalidateUrl == null) {
            invalidateUrl = String.format("%s/invalidate", getBaseUrl());
        }
        return String.format("%s/%s", invalidateUrl, token);
    }
    
    public Authentication authenticate(final String username, final String password) {
        TokenRequest rq = new TokenRequest(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<TokenRequest> entity = new HttpEntity<TokenRequest>(rq, headers);

        ResponseEntity<Authentication> token = getRestTemplate().exchange(getAuthenticateUrl(), POST, entity, Authentication.class);
        return (Authentication)token.getBody();
    }
    public Authentication validate(final String token) {


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.TEXT_PLAIN);

        try {
            Authentication auth = getRestTemplate().getForObject(getValidateUrl(token), Authentication.class);
            keepTokenAlive(token);
            return auth;
        }
        catch (ResourceAccessException t) {
            throw new ServiceUnavailableException(t.getMessage(), t);
        }
    }
    public void invalidate(final String token) {


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.TEXT_PLAIN);

        try {
            getRestTemplate().exchange(getInvalidateUrl(token), HttpMethod.PUT, null, Void.class);
        }
        catch (ResourceAccessException t) {
            throw new ServiceUnavailableException(t.getMessage(), t);
        }
    }

    private void keepTokenAlive(final String token) {
        if (jmsTemplate != null && keepTokenAliveTopic != null) {
            jmsTemplate.send(keepTokenAliveTopic, new MessageCreator() {
                
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(new KeepTokenAliveEvent(token));
                }
            });
        }
    }
}
