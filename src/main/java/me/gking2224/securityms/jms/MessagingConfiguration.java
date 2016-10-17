package me.gking2224.securityms.jms;

import static me.gking2224.common.jms.CommonMessagingConfiguration.TOPIC_LISTENER_CONTAINER_FACTORY;
import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.core.AuthenticationException;

import me.gking2224.common.jms.CommonMessagingConfiguration;
import me.gking2224.securityms.client.KeepTokenAliveEvent;
import me.gking2224.securityms.service.AuthenticationService;

@Import(CommonMessagingConfiguration.class)
public class MessagingConfiguration {
    
    private Logger LOG = LoggerFactory.getLogger(MessagingConfiguration.class);

    @Autowired AuthenticationService authService;
    @JmsListener(destination = KEEP_TOKEN_ALIVE_TOPIC, containerFactory=TOPIC_LISTENER_CONTAINER_FACTORY)
    public void keepTokenAliveListener(ObjectMessage message) throws JMSException {
        String token = ((KeepTokenAliveEvent)message.getObject()).getToken();
        try {
            authService.keepTokenAlive(token);
        }
        catch (AuthenticationException e) {
            LOG.info("Authentication exception enountered ({}), keep alive failed for token {}", e.getMessage(), token);
        }
    }
}
