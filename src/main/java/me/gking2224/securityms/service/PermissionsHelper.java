package me.gking2224.securityms.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import me.gking2224.securityms.model.Permission;

public class PermissionsHelper {

    public static Set<String> getEffectivePermissions(Set<Permission> permissions) {

        final Set<Permission> positive = new HashSet<Permission>();
        final Set<Permission> negative = new HashSet<Permission>();

        permissions.forEach(p-> traversePermission(positive, negative, p));
        negative.forEach(p -> {
            positive.remove(p);
        });
        
        return positive.stream().map(Permission::getName).collect(Collectors.toSet());
    }

    private static void traversePermission(
            final Set<Permission> rv, final Set<Permission> negative, final Permission p) {
        if (!p.isEnabled()) {
            negative.add(p);
        }
        else rv.add(p);
        p.getIncludes().forEach(c-> traversePermission(rv, negative, c));
    }

}
