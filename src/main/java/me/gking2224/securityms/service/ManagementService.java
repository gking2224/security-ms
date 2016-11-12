package me.gking2224.securityms.service;

import java.util.Set;

import me.gking2224.securityms.model.User;

public interface ManagementService {

    int cleanExpiredTokens();

    int invalidateUserSession(String username, String comment);

    String resetUserPassword(String username);

    Set<String> getUserEffectivePermissions(String username);

    void disableUserAccount(String username);
    
    void enableUserAccount(String username);

    void createRole(String name);

    void createPermission(String name, String parent);

    void grantUserPermission(String username, String permission);

    void grantRolePermission(String role, String permission);

    void updateUserPassword(String username, String password);

    void createUser(String username, String firstName, String surname);

    void grantUserRole(String username, String role);

}
