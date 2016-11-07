package me.gking2224.securityms.client;

import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_EXPIRED_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_INVALIDATED_TOPIC;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import me.gking2224.common.client.MicroServiceEnvironment;

@Component
public class SecurityServiceClientFactory {
    
    private static final String DEFAULT_SECURITY_SERVICE_HOST = "localhost";
    private static final Integer DEFAULT_SECURITY_SERVICE_PORT = 11000;
    private static final String DEFAULT_SECURITY_SERVICE_CONTEXT = "security";
    
    @Autowired MicroServiceEnvironment env;

    @Bean
    public SecurityServiceClient securityClient(
            final RestTemplate restTemplate,
            @Autowired @Qualifier("pubSubTemplate") final JmsTemplate pubSubTemplate,
            @Qualifier(KEEP_TOKEN_ALIVE_TOPIC) Topic keepTokenAliveTopic,
            @Qualifier(TOKEN_INVALIDATED_TOPIC) Topic tokenInvalidatedTopic,
            @Qualifier(TOKEN_EXPIRED_TOPIC) Topic tokenExpiredTopic
    ) throws Exception {
        SecurityServiceClient client = new SecurityServiceClient();
        client.setHost(env.getProperty("security.securityService.host", DEFAULT_SECURITY_SERVICE_HOST));
        client.setPort(env.getProperty("security.securityService.port", Integer.class, DEFAULT_SECURITY_SERVICE_PORT));
        client.setContext(env.getProperty("security.securityService.context", DEFAULT_SECURITY_SERVICE_CONTEXT));
        client.setRestTemplate(restTemplate);
        client.setJmsTemplate(pubSubTemplate);
        client.setKeepTokenAliveTopic(keepTokenAliveTopic);
        client.setTokenInvalidatedTopic(tokenInvalidatedTopic);
        client.setTokenExpiredTopic(tokenExpiredTopic);
        client.afterPropertiesSet();
        return client;
    }
    
    @Bean(KEEP_TOKEN_ALIVE_TOPIC)
    public Topic keepTokenAlive() {
        return new ActiveMQTopic(KEEP_TOKEN_ALIVE_TOPIC);
    }
    
    @Bean(TOKEN_INVALIDATED_TOPIC)
    public Topic tokenInvalidatedTopic() {
        return new ActiveMQTopic(TOKEN_INVALIDATED_TOPIC);
    }
    
    @Bean(TOKEN_EXPIRED_TOPIC)
    public Topic tokenExpiredTopic() {
        return new ActiveMQTopic(TOKEN_EXPIRED_TOPIC);
    }
}
