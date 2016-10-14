package me.gking2224.securityms.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.AbstractDaoImpl;
import me.gking2224.securityms.db.jpa.UserRepository;
import me.gking2224.securityms.model.User;

@Component
@Transactional
public class UserDaoImpl extends AbstractDaoImpl<User, Long> implements UserDao {

    @Autowired
    protected UserRepository repository;
    
    public UserDaoImpl() {
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
    @Override
    protected JpaRepository<User, Long> getRepository() {
        return repository;
    }
}
