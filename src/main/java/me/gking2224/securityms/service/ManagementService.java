package me.gking2224.securityms.service;

import java.util.Set;

public interface ManagementService {

    int cleanExpiredTokens();

    int invalidateUserSession(String username, String comment);

    String resetUserPassword(String username);

    Set<String> getUserEffectivePermissions(String username);

    void disableUserAccount(String username);

}
