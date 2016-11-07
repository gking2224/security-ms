package me.gking2224.securityms.client;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.ResourceAccessException;

import me.gking2224.common.client.AbstractServiceClient;

public class SecurityServiceClient extends AbstractServiceClient {
    
    public static final String KEEP_TOKEN_ALIVE_TOPIC = "KeepTokenAlive";
    public static final String TOKEN_INVALIDATED_TOPIC = "TokenInvalidated";
    public static final String TOKEN_EXPIRED_TOPIC = "TokenExpired";
    
    private String host;
    private int port;
    private String context;
    private String createTokenUrl;
    private String validateUrl;
    private String invalidateUrl;
    
    private JmsTemplate jmsTemplate;
    private Topic keepTokenAliveTopic;
    private Topic tokenInvalidatedTopic;
    private Topic tokenExpiredTopic;
    
    private Map<String, AuthenticationException> invalidatedTokens = new HashMap<String, AuthenticationException>();

    public SecurityServiceClient() {
        super();
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

    public Topic getTokenInvalidatedTopic() {
        return tokenInvalidatedTopic;
    }

    public void setTokenInvalidatedTopic(Topic tokenInvalidatedTopic) {
        this.tokenInvalidatedTopic = tokenInvalidatedTopic;
    }
    
    public Topic getTokenExpiredTopic() {
        return tokenExpiredTopic;
    }

    public void setTokenExpiredTopic(Topic tokenExpiredTopic) {
        this.tokenExpiredTopic = tokenExpiredTopic;
    }

    protected String getValidateUrl(final String token) {
        return String.format("%s/%s", validateUrl, token);
    }

    protected String getInvalidateUrl(final String token) {
        return String.format("%s/%s", invalidateUrl, token);
    }
    
    public Authentication authenticate(final String username, final String password) {
        TokenRequest rq = new TokenRequest(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<TokenRequest> entity = new HttpEntity<TokenRequest>(rq, headers);

        ResponseEntity<Authentication> token = getRestTemplate().exchange(createTokenUrl, POST, entity, Authentication.class);
        return (Authentication)token.getBody();
    }
    
    public Authentication validate(final String token) {

        checkTokenNotInvalidated(token);

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
        catch (Throwable t) {
            throw t;
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

    private void checkTokenNotInvalidated(String token) {
        if (invalidatedTokens.containsKey(token)) {
            AuthenticationException e = invalidatedTokens.get(token);
            throw e;
        }
    }

    private void keepTokenAlive(final String token) {
        if (jmsTemplate != null && keepTokenAliveTopic != null) {
            jmsTemplate.send(keepTokenAliveTopic, new MessageCreator() {
                
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(token);
                }
            });
        }
    }

    /**
     * Notification that we should fail-fast a future valiation request for this token
     * @param token
     * @param authenticationException 
     */
    public void tokenInvalidated(final String token, AuthenticationException e) {
        invalidatedTokens.put(token, e);
    }

    /**
     * Notification that we should fail-fast a future valiation request for this token
     * @param token
     * @param authenticationException 
     */
    public void tokenExpired(final String token) {
        invalidatedTokens.remove(token);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createTokenUrl = String.format("%s/authenticate", getBaseUrl());
        validateUrl = String.format("%s/validate", getBaseUrl());
        invalidateUrl = String.format("%s/invalidate", getBaseUrl());
    }
}
