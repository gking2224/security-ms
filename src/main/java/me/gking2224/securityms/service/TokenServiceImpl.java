package me.gking2224.securityms.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import me.gking2224.securityms.client.InvalidTokenException;

@Component
public class TokenServiceImpl implements TokenService {

    @Autowired
    org.springframework.security.core.token.TokenService delegate;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Override
    public TokenBuilder newToken() {
        return new DefaultTokenBuilder();
    }

    @Override
    public Token allocateToken(String extendedInformation) {
        return delegate.allocateToken(extendedInformation);
    }

    @Override
    public Token verifyToken(String key) {
        try {
            return delegate.verifyToken(key);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidTokenException(e.getMessage(), e);
        }
    }

    @Override
    public Token allocateToken(me.gking2224.securityms.model.Token<?> token) {
        return allocateToken(tokenToString(token));
    }
    
    @Override
    public String tokenToString(me.gking2224.securityms.model.Token<?> token) {
        
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            ObjectOutput oo = new ObjectOutputStream(bos);
            oo.writeObject(token.getClass().getName());
            token.writeExternal(oo);
            oo.flush();
            return encryptionService.toBase64(bos.toByteArray());
        }
        catch (IOException e) {
            throw new InternalAuthenticationServiceException("Error creating token", e);
        }
    }
    
    @Override
    public me.gking2224.securityms.model.Token<?> tokenFromString(final String k) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(encryptionService.fromBase64(k));
            ObjectInput oi;
                oi = new ObjectInputStream(bis);
            
            String tokenClass = (String)oi.readObject();
            me.gking2224.securityms.model.Token<?> token =
                    (me.gking2224.securityms.model.Token<?>)Class.forName(tokenClass).newInstance();
            
            token.readExternal(oi);
            return token;
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new InternalAuthenticationServiceException("Error parsing token", e);
        }
        
    }

}
