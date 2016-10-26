package me.gking2224.securityms.security;

import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_EXPIRED_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_INVALIDATED_TOPIC;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import me.gking2224.securityms.client.SecurityEvent;
import me.gking2224.securityms.client.TokenExpiredMessage;
import me.gking2224.securityms.client.TokenInvalidatedMessage;
import me.gking2224.securityms.service.SecurityEventListener;

@Component
public class BroadcastingSecurityEventListener implements SecurityEventListener {
    
    @Autowired @Qualifier("pubSubTemplate") JmsTemplate pubSubTemplate;
    
    @Autowired @Qualifier(TOKEN_INVALIDATED_TOPIC) Topic tokenInvalidatedTopic;
    
    @Autowired @Qualifier(TOKEN_EXPIRED_TOPIC) Topic tokenExpiredTopic;
    
    @Override
    public void onEvent(SecurityEvent event) {
        if (TokenInvalidatedMessage.class.isAssignableFrom(event.getClass()))
            tokenInvalidated((TokenInvalidatedMessage)event);
        if (TokenExpiredMessage.class.isAssignableFrom(event.getClass()))
            tokenExpired((TokenExpiredMessage)event);
    }
    
    private void tokenInvalidated(TokenInvalidatedMessage event) {
        sendMessage(tokenInvalidatedTopic, event);
    }
    
    private void tokenExpired(TokenExpiredMessage event) {
        sendMessage(tokenExpiredTopic, event);
    }
    
    private void sendMessage(Topic topic, Serializable message) {
        pubSubTemplate.send(topic, new MessageCreator() {
            
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage om = session.createObjectMessage();
                om.setObject(message);
                return om;
            }
        });
    }

}
