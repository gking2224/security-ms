package me.gking2224.securityms.db.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.securityms.SecurityMicroServiceTestConfiguration;
import me.gking2224.securityms.SecurityServiceTestInitializer;
import me.gking2224.securityms.model.Permission;
import me.gking2224.securityms.model.Role;
import me.gking2224.securityms.model.RolePermission;
import me.gking2224.securityms.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name="securityms", classes=SecurityMicroServiceTestConfiguration.class, initializers={SecurityServiceTestInitializer.class})
@Transactional
@Rollback
public class UserRepositoryIT {

    @Autowired
    protected UserRepository repository;
    
    @Test
    public void testSave() {
        String name = "user";
        
        User u = new User();
        u.setUsername(name);
        u.setFirstName("Donald");
        u.setSurname("Trump");
        User saved = repository.save(u);
        assertNotNull(saved);
        assertEquals(name, saved.getUsername());
    }
    
    @Test
    @Sql("../../SingleUser.sql")
    public void testFindOne() {
        
        User u = repository.findOne(100L);
        
        assertNotNull(u);
        assertEquals("test", u.getUsername());
        
        Set<Role> roles = u.getRoles();
        assertNotNull(roles);
        assertEquals(1, roles.size());
        Role role = roles.iterator().next();
        assertEquals("Test Role",  role.getName());
        
        Set<RolePermission> perms = role.getRolePermissions();
        assertNotNull(perms);
        assertEquals(1, perms.size());
        RolePermission perm = perms.iterator().next();
        assertEquals("Parent Permission", perm.getPermission().getName());
        
        Set<Permission> childPerms = perm.getPermission().getIncludes();
        assertNotNull(childPerms);
        assertEquals(1, childPerms.size());
        Permission childPerm = childPerms.iterator().next();
        assertEquals("Child Permission", childPerm.getName());
        
        
        Set<String> allPermissions = u.getEffectivePermissions();
        assertTrue(allPermissions.contains("Parent Permission"));
        assertTrue(allPermissions.contains("Child Permission"));
    }
}
