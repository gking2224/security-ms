package me.gking2224.securityms.service;

import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.utils.DurationFormatter;
import me.gking2224.securityms.client.Authentication;
import me.gking2224.securityms.client.InvalidTokenException;
import me.gking2224.securityms.client.TokenExpiredException;
import me.gking2224.securityms.client.TokenInvalidatedException;
import me.gking2224.securityms.db.dao.TokenDao;
import me.gking2224.securityms.db.dao.UserDao;
import me.gking2224.securityms.model.Token;
import me.gking2224.securityms.model.User;

@Component
@Transactional(readOnly=true)
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    TokenService ts;

    @Autowired
    public PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private TokenDao tokenDao;
    
    private Clock clock = Clock.systemDefaultZone();
    
    private Duration timeoutPeriod = DurationFormatter.getInstance().apply("30m");
    
    @Override
    @Transactional(readOnly=false)
    public Authentication authenticate(final String username, final String password) throws AuthenticationException {
        User u = getUser(username);
        
        checkPassword(u.getPassword(), password, username);
        
        Token tokenEntity = new Token();
        Long expiry = getTimeout();
        tokenEntity.setExpiry(expiry);
        tokenEntity.setUser(u);
        tokenEntity = tokenDao.save(tokenEntity);
        
        org.springframework.security.core.token.Token token = ts.allocateToken(tokenEntity.getId().toString());
        String tokenStr = token.getKey();
        
        tokenEntity.setToken(tokenStr);
        tokenDao.save(tokenEntity);

        Authentication rv = createAuthentication(tokenEntity);
        rv.setAuthenticated(true);
        return rv;
    }

    private User getUser(final String username) {
        User u = userDao.findByUsername(username);
        
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        validateUserAccount(u);
        return u;
    }
    
    @Override
    public Authentication validate(final String key) throws AuthenticationException {
        org.springframework.security.core.token.Token t = verifyToken(key);
        Long tokenId = Long.valueOf(t.getExtendedInformation());

        Token tokenEntity = getTokenEntity(tokenId);
        
        Authentication rv = createAuthentication(tokenEntity);
        rv.setAuthenticated(true);
        return rv;
    }

    @Override
    @Transactional(readOnly=false)
    public void invalidate(String key) {
        org.springframework.security.core.token.Token t = verifyToken(key);
        Long tokenId = Long.valueOf(t.getExtendedInformation());

        try {
            Token token = getTokenEntity(tokenId);
            token.setValid(false);
            tokenDao.save(token);
        }
        catch (TokenExpiredException e) {
            // ignore
        }
    }

    private org.springframework.security.core.token.Token verifyToken(final String key) {
        org.springframework.security.core.token.Token token = null;
        
        Throwable cause = null;
        try {
            token = ts.verifyToken(key);
        }
        catch (Throwable t) {
            cause = t;
        }
        if (token == null) {
            throw new InvalidTokenException(key, cause);
        }
            
        return token;
    }

    protected Authentication createAuthentication(final Token token) {
        
        User user = token.getUser();
        Set<String> perms = user.getEffectivePermissions();
        
        validateUserAccount(user);
        
        Set<String> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        return new Authentication(token.getToken(), user.getUserDetails(), roles, perms, token.getExpiry());
    }
    
    @JmsListener(destination=KEEP_TOKEN_ALIVE_TOPIC)
    @Transactional(readOnly=false)
    @Override
    public void keepTokenAlive(final String token) {
        org.springframework.security.core.token.Token t = verifyToken(token);
        Long tokenId = Long.valueOf(t.getExtendedInformation());

        Token tokenEntity = getTokenEntity(tokenId);
        tokenEntity.setExpiry(getTimeout());
        tokenDao.save(tokenEntity);
    }

    /**
     * Loads the given token from the database.
     * 
     * @return the persisted token, never null
     * @throws TokenExpiredException if token not found
     * @throws AuthenticationException if token is expired or invalid
     */
    private Token getTokenEntity(final Long tokenId) {
        Token token = tokenDao.findById(tokenId);
        if (token == null) { // assume cleaned up as expired
            throw new TokenExpiredException("Token not found - assume expired");
        }

        validateToken(token);
        return token;
    }

    private void validateToken(final Token token) {
        if (!token.isValid()) {
            throw new TokenInvalidatedException(token.getInvalidationComment());
        }
        else if (Instant.now(clock).isAfter(Instant.ofEpochMilli(token.getExpiry()))) {
            throw new TokenExpiredException("Token expired");
        }
    }

    private void validateUserAccount(final User user) {
        if (!user.isEnabled()) {
            throw new DisabledException(user.getUsername());
        }
        
    }

    private Long getTimeout() {
        Duration timeoutPeriod = this.timeoutPeriod;
        Instant now = Instant.now(clock);
        Instant plus = now.plus(timeoutPeriod);
        return plus.toEpochMilli();
    }

    private void checkPassword(final String storedPassword, final String givenPassword, final String username) {
        
        if (!passwordEncoder.matches(givenPassword, storedPassword)) {
            throw new BadCredentialsException("Given password is incorrect");
        }
        
    }
}
