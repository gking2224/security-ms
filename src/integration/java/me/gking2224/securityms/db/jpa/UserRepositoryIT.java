package me.gking2224.securityms.db.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.securityms.TestConfiguration;
import me.gking2224.securityms.model.Permission;
import me.gking2224.securityms.model.Role;
import me.gking2224.securityms.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"embedded"})
@ContextConfiguration(classes=TestConfiguration.class)
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
        User saved = repository.save(u);
        assertNotNull(saved);
        assertEquals(name, saved.getUsername());
    }
    
    @Test
    @Sql("../../SingleUser.sql")
    public void testFindOne() {
        
        User u = repository.findOne(1L);
        
        assertNotNull(u);
        assertEquals("Test User", u.getUsername());
        
        Set<Role> roles = u.getRoles();
        assertNotNull(roles);
        assertEquals(1, roles.size());
        Role role = roles.iterator().next();
        assertEquals("Test Role",  role.getName());
        
        Set<Permission> perms = role.getPermissions();
        assertNotNull(perms);
        assertEquals(1, perms.size());
        Permission perm = perms.iterator().next();
        assertEquals("Parent Permission", perm.getName());
        
        Set<Permission> childPerms = perm.getIncludes();
        assertNotNull(childPerms);
        assertEquals(1, childPerms.size());
        Permission childPerm = childPerms.iterator().next();
        assertEquals("Child Permission", childPerm.getName());
        
        
        Set<String> allPermissions = u.getEffectivePermissions();
        assertTrue(allPermissions.contains("Parent Permission"));
        assertTrue(allPermissions.contains("Child Permission"));
    }
}
