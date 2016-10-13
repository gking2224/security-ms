package me.gking2224.securityms.db.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.AbstractDaoImpl;
import me.gking2224.securityms.db.jpa.UserRepository;
import me.gking2224.securityms.model.User;

@ManagedResource(objectName="dao:name=thingDaoImpl", description="UserDao", log=true,
logFile="jmx.log", currencyTimeLimit=15, persistPolicy="Never")
@Component
@Transactional
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao {

    @Autowired
    protected UserRepository userRepository;
    
    private int lastLoadCount = 0;
    
    public UserDaoImpl() {
    }

    @Override
    public User create(User user) {
        User saved = userRepository.save(user);
        return saved;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        this.lastLoadCount = users.size();
        return users;
    }

    @Override
    public User update(User user) {
        User saved = userRepository.save(user);
        return saved;
    }
    
    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findOne(id);
        return user;
    }

    @ManagedAttribute
    public int getLastLoadCount() {
        return lastLoadCount;
    }
}
