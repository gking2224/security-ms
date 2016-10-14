package me.gking2224.securityms.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.utils.DurationFormatter;
import me.gking2224.securityms.client.Authentication;
import me.gking2224.securityms.client.TokenExpiredException;
import me.gking2224.securityms.db.dao.UserDao;
import me.gking2224.securityms.model.Token;
import me.gking2224.securityms.model.User;

@Component
@Transactional(readOnly=true)
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    public PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDao userDao;
    
    private Clock clock = Clock.systemDefaultZone();
    
    private Duration timeoutPeriod = DurationFormatter.getInstance().apply("30m");
    
    @Override
    public Authentication authenticate(final String username, final String password) throws AuthenticationException {
        User u = userDao.findByUsername(username);
        
//        for (int i = 0; i < 5; i++) {
//            logger.debug(passwordEncoder.encode(password));
//        }
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        else if (!u.isEnabled()) {
            throw new DisabledException(username);
        }
        
        checkPassword(u.getPassword(), password, username);
        
        Token<?> token = tokenService.newToken()
                .forUser(username)
                .withExpiry(getTimeout())
                .buildToken();
        
        org.springframework.security.core.token.Token allocateToken = tokenService.allocateToken(token);
        Authentication rv = createAuthentication(u, token, allocateToken.getKey());
        rv.setAuthenticated(true);
        return rv;
    }
    
    @Override
    public Authentication validate(String key) throws AuthenticationException {
        org.springframework.security.core.token.Token t = tokenService.verifyToken(key);
        Token<?> token = tokenService.tokenFromString(t.getExtendedInformation());

        User u = userDao.findByUsername(token.getUsername());
        Authentication rv = createAuthentication(u, token, key);
        rv.setAuthenticated(true);
        return rv;
    }

    protected Authentication createAuthentication(final User user, final Token<?> token, final String key) {
        
        Set<String> perms = user.getEffectivePermissions();
        
        checkUserAccount(user);
        checkTokenExpiry(token);
        
        Set<String> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        return new Authentication(key, user.getUserDetails(), roles, perms, token.getTimeout());
    }

    private void checkTokenExpiry(Token<?> token) {
        if (Instant.now(clock).isAfter(token.getTimeout())) {
            throw new TokenExpiredException("Token expired");
        }
    }

    private void checkUserAccount(User user) {
        if (!user.isEnabled()) {
            throw new DisabledException("Account disabled for");
        }
        
    }

    private Instant getTimeout() {
        Duration timeoutPeriod = this.timeoutPeriod;
        Instant now = Instant.now(clock);
        Instant plus = now.plus(timeoutPeriod);
        return plus;
    }

    private void checkPassword(final String storedPassword, final String givenPassword, final String username) {
        
        if (!passwordEncoder.matches(givenPassword, storedPassword)) {
            throw new BadCredentialsException("Given password is incorrect");
        }
        
    }

}
