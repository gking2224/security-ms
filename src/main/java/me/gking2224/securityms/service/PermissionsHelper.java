package me.gking2224.securityms.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import me.gking2224.securityms.model.AssignedPermission;
import me.gking2224.securityms.model.Permission;

public class PermissionsHelper {

    public static Set<String> getEffectivePermissions(Set<AssignedPermission> permissions) {

        final Set<Permission> positive = new HashSet<Permission>();
        final Set<Permission> negative = new HashSet<Permission>();

        permissions.forEach(p-> traversePermission(p.isEnabled() ? positive : negative, p.getPermission()));
        negative.forEach(p -> {
            positive.remove(p);
        });
        
        return positive.stream().map(Permission::getName).collect(Collectors.toSet());
    }

    private static void traversePermission(
            final Set<Permission> permissions, final Permission p) {
        permissions.add(p);
        p.getIncludes().forEach(i-> traversePermission(permissions, i));
    }

}
