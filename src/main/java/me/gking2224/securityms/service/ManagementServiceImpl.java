package me.gking2224.securityms.service;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.utils.RandomString;
import me.gking2224.securityms.client.TokenExpiredMessage;
import me.gking2224.securityms.client.TokenInvalidatedException;
import me.gking2224.securityms.client.TokenInvalidatedMessage;
import me.gking2224.securityms.db.dao.TokenDao;
import me.gking2224.securityms.db.dao.UserDao;
import me.gking2224.securityms.model.User;

@ManagedResource(objectName="service:name=ManagementService", description="Management Service",
log=true, logFile="jmx.log", persistPolicy="Never")
@Component
@Transactional(readOnly=true)
public class ManagementServiceImpl implements ManagementService {
    
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(ManagementServiceImpl.class);

    @Autowired
    public PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private TokenDao tokenDao;
    
    
    @Autowired SecurityEventListener eventListener;

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Clean Expired Tokens")
    public int cleanExpiredTokens() {
        List<String> deleted = tokenDao.deleteExpired();
        deleted.forEach(t->notifyExpired(t));
        return deleted.size();
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Invalidate User Session")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username"),
        @ManagedOperationParameter(name="comment", description="Comment (to be sent to user)")
    })
    public int invalidateUserSession(final String username, final String comment) {
        List<String> tokens = tokenDao.invalidateUserSession(username, comment);
        tokens.forEach(t->notifyInvalidated(t, comment));
        return tokens.size();
    }
    
    private void notifyInvalidated(final String token, final String msg) {
        eventListener.onEvent(new TokenInvalidatedMessage(token, new TokenInvalidatedException(msg)));
    }
    
    private void notifyExpired(final String token) {
        eventListener.onEvent(new TokenExpiredMessage(token));
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Reset user password")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username")
    })
    public String resetUserPassword(final String username) {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        String newPwd = RandomString.asBase64(10);
        u.setPassword(passwordEncoder.encode(newPwd));
        return newPwd;
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Get user effective permissions")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username")
    })
    public Set<String> getUserEffectivePermissions(final String username) {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        return u.getEffectivePermissions();
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Disable user account")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username")
    })
    public void disableUserAccount(final String username) {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        u.setEnabled(false);
    }

}
