package me.gking2224.securityms.service;

import static me.gking2224.common.jmx.CommonJmxConfiguration.JMX_MBEAN_GROUP_NAME_PREFIX;

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
import me.gking2224.securityms.db.dao.PermissionDao;
import me.gking2224.securityms.db.dao.RoleDao;
import me.gking2224.securityms.db.dao.TokenDao;
import me.gking2224.securityms.db.dao.UserDao;
import me.gking2224.securityms.model.Permission;
import me.gking2224.securityms.model.Role;
import me.gking2224.securityms.model.User;

@ManagedResource(objectName=ManagementServiceImpl.SERVICE_NAME, description="Management Service",
log=true, logFile="jmx.log", persistPolicy="Never")
@Component
@Transactional(readOnly=true)
public class ManagementServiceImpl implements ManagementService {
    
    public static final String SERVICE_NAME = JMX_MBEAN_GROUP_NAME_PREFIX + "ManagementService";
    
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(ManagementServiceImpl.class);

    @Autowired
    public PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private TokenDao tokenDao;
    
    @Autowired
    private PermissionDao permissionDao;
    
    
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

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Create user")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username"),
        @ManagedOperationParameter(name="firstName", description="First name"),
        @ManagedOperationParameter(name="surname", description="Surname")
    })
    public void createUser(final String username, final String firstName, final String surname) {
        User u = new User();
        u.setUsername(username);
        u.setFirstName(firstName);
        u.setSurname(surname);
        u.setEnabled(false);
        userDao.save(u);
    }
    
    private void notifyExpired(final String token) {
        eventListener.onEvent(new TokenExpiredMessage(token));
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Reset user password to a random value")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username")
    })
    public String resetUserPassword(final String username) {
        String newPwd = RandomString.asBase64(10);
        updateUserPassword(username, newPwd);
        return newPwd;
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Update user password")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username"),
        @ManagedOperationParameter(name="password", description="New password")
    })
    public void updateUserPassword(final String username, final String password) {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        u.setPassword(passwordEncoder.encode(password));
        userDao.save(u);
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Create role")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="name", description="Role name")
    })
    public void createRole(final String name) {
        Role r = new Role();
        r.setName(name);
        roleDao.save(r);
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Create permission")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="name", description="Permission name"),
        @ManagedOperationParameter(name="parent", description="Parent name")
    })
    public void createPermission(final String name, final String parent) {
        Permission parentP = (parent == null) ? null : permissionDao.findExisting(parent);
        Permission p = new Permission();
        p.setName(name);
        p.setParent(parentP);
        permissionDao.save(p);
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Grant user permission")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username"),
        @ManagedOperationParameter(name="permission", description="Permission name")
    })
    public void grantUserPermission(final String username, final String permission) {
        Permission p = permissionDao.findExisting(permission);
        User u = userDao.findByUsername(username);
        u.addPermission(p);
        userDao.save(u);
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Grant user role")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username"),
        @ManagedOperationParameter(name="role", description="Role name")
    })
    public void grantUserRole(final String username, final String role) {
        Role r = roleDao.findByName(role);
        User u = userDao.findByUsername(username);
        u.addRole(r);
        userDao.save(u);
    }

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Grant role permission")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="role", description="Role"),
        @ManagedOperationParameter(name="permission", description="Permission name")
    })
    public void grantRolePermission(final String role, final String permission) {
        Permission p = permissionDao.findExisting(permission);
        Role r = roleDao.findByName(role);
        r.addPermission(p);
        roleDao.save(r);
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

    @Override
    @Transactional(readOnly=false)
    @ManagedOperation(description="Enable user account")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="username", description="Username")
    })
    public void enableUserAccount(final String username) {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        u.setEnabled(true);
        userDao.save(u);
    }

}
