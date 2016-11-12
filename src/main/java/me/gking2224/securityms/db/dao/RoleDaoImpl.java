package me.gking2224.securityms.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.AbstractDaoImpl;
import me.gking2224.securityms.db.jpa.RoleRepository;
import me.gking2224.securityms.model.Role;

@Component
@Transactional
public class RoleDaoImpl extends AbstractDaoImpl<Role, Long> implements RoleDao {

    @Autowired
    protected RoleRepository repository;
    
    public RoleDaoImpl() {
    }
    
    @Override
    protected JpaRepository<Role, Long> getRepository() {
        return repository;
    }

    @Override
    public Role findByName(String role) {
        return repository.findByName(role);
    }
}
