package me.gking2224.securityms.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.AbstractDaoImpl;
import me.gking2224.securityms.db.jpa.PermissionRepository;
import me.gking2224.securityms.model.Permission;

@Component
@Transactional
public class PermissionDaoImpl extends AbstractDaoImpl<Permission, Long> implements PermissionDao {

    @Autowired
    protected PermissionRepository repository;
    
    public PermissionDaoImpl() {
    }
    
    @Override
    protected JpaRepository<Permission, Long> getRepository() {
        return repository;
    }

    @Override
    public Permission findExisting(String name) {
        return repository.findByName(name);
    }
}
