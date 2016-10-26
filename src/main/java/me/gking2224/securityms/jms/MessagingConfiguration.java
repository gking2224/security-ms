package me.gking2224.securityms.jms;

import static me.gking2224.common.client.jms.CommonMessagingConfiguration.TOPIC_LISTENER_CONTAINER_FACTORY;
import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.core.AuthenticationException;

import me.gking2224.securityms.client.TokenInvalidatedMessage;
import me.gking2224.securityms.service.AuthenticationService;
import me.gking2224.securityms.service.SecurityEventListener;

public class MessagingConfiguration {
    
    private Logger LOG = LoggerFactory.getLogger(MessagingConfiguration.class);

    @Autowired AuthenticationService authService;
    
    @Autowired SecurityEventListener securityEventListener;
    
    @JmsListener(destination = KEEP_TOKEN_ALIVE_TOPIC, containerFactory=TOPIC_LISTENER_CONTAINER_FACTORY)
    public void keepTokenAliveListener(TextMessage message) throws JMSException {
        String token = message.getText();
        try {
            authService.keepTokenAlive(token);
        }
        catch (AuthenticationException e) {
            LOG.info("Authentication exception enountered ({}), keep alive failed for token {}", e.getMessage(), token);
            securityEventListener.onEvent(new TokenInvalidatedMessage(token, e));
        }
    }
}
