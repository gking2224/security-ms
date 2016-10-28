package me.gking2224.securityms.security;

import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_EXPIRED_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_INVALIDATED_TOPIC;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Enumeration;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;

import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.utils.RandomString;

@ComponentScan("me.gking2224.securityms.security")
@Configuration
public class SecurityConfiguration {
    
    private Charset charset = StandardCharsets.UTF_8;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationEventPublisher authEventPublisher;

    @Autowired
    private ObjectPostProcessor<Object> objectPostProcessor;
    
    @Autowired @Qualifier("pubSubTemplate") JmsTemplate pubSubTemplate;
    
    @Autowired @Qualifier(TOKEN_INVALIDATED_TOPIC) Topic sessionInvalidatedTopic;
    
    @Autowired MicroServiceEnvironment env;

    private String encryptionPassword;

    private String encryptionSalt;

    protected byte[] getBytes(final String s) {
        return s.getBytes(charset);
    }
    
    @Bean
    public BytesEncryptor encryptor() {
        return Encryptors.stronger(encryptionPassword(), encryptionSalt());
    }

    private String encryptionPassword() {
        if (this.encryptionPassword == null) {
            this.encryptionPassword = env.getRequiredProperty("security.encryptionPassword");
        }
        return this.encryptionPassword;
    }

    private String encryptionSalt() {
        if (this.encryptionSalt == null) {
            this.encryptionSalt = RandomString.asHex(24);//env.getRequiredProperty("security.encryptionSalt");
        }
        return this.encryptionSalt;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
    
    @Bean
    public TokenService tokenService() {
        KeyBasedPersistenceTokenService rv = new KeyBasedPersistenceTokenService();
        rv.setPseudoRandomNumberBytes(137);
        int serverInteger = 999;
        try {
            Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
            NetworkInterface net0 = net.nextElement();
            byte[] hardwareAddress = net0.getHardwareAddress();
            serverInteger = ByteBuffer.wrap(hardwareAddress).getInt();
        } catch (Throwable t) {
        }
        rv.setServerInteger(serverInteger);
        rv.setSecureRandom(new SecureRandom(getBytes(encryptionSalt())));
        rv.setServerSecret(encryptionPassword());
        return rv;
    }
    
    @Bean(KEEP_TOKEN_ALIVE_TOPIC)
    public Topic keepTokenAlive() {
        ActiveMQTopic t = new ActiveMQTopic(KEEP_TOKEN_ALIVE_TOPIC);
        return t;
    }
    
    @Bean(TOKEN_INVALIDATED_TOPIC)
    public Topic tokenInvalidatedTopic() {
        return new ActiveMQTopic(TOKEN_INVALIDATED_TOPIC);
    }
    
    @Bean(TOKEN_EXPIRED_TOPIC)
    public Topic tokenExpiredTopic() {
        return new ActiveMQTopic(TOKEN_EXPIRED_TOPIC);
    }
    
    @Bean
    public Charset charset() {
        return charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public AuthenticationEventPublisher getAuthEventPublisher() {
        return authEventPublisher;
    }

    public void setAuthEventPublisher(AuthenticationEventPublisher authEventPublisher) {
        this.authEventPublisher = authEventPublisher;
    }

    public ObjectPostProcessor<Object> getObjectPostProcessor() {
        return objectPostProcessor;
    }

    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    public void setEncryptionPassword(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }
}
