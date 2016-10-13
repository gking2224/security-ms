package me.gking2224.securityms;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableWebSecurity(debug=true)
@PropertySource("/security.properties")
public class SecurityConfiguration {//extends WebSecurityConfigurerAdapter {
    
    @Value("${encryptionSalt}")
    private String encryptionSalt;
    
    @Value("${encryptionPassword}")
    private String encryptionPassword;
    
    private Charset charset = StandardCharsets.UTF_8;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationEventPublisher authEventPublisher;

    @Autowired
    private ObjectPostProcessor<Object> objectPostProcessor;

    protected byte[] getBytes(final String s) {
        return s.getBytes(charset);
    }
    
    @Bean
    public BytesEncryptor encryptor() {
        return Encryptors.stronger(encryptionPassword, encryptionSalt);
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
        rv.setSecureRandom(new SecureRandom(getBytes(encryptionSalt)));
        rv.setServerSecret(encryptionPassword);
        return rv;
    }
    
    @Bean
    public Charset charset() {
        return charset;
    }

    public String getEncryptionSalt() {
        return encryptionSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    public String getEncryptionPassword() {
        return encryptionPassword;
    }

    public void setEncryptionPassword(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
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
}
