package me.gking2224.securityms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.securityms.db.dao.UserDao;
import me.gking2224.securityms.model.User;

@Component
@Transactional(readOnly=true)
public class ThingServiceImpl implements ThingService {

    @Autowired
    private UserDao dao;

    public ThingServiceImpl() {
    }

    @Override
    @Transactional(readOnly=false)
    public User create(User user) {
        return dao.create(user);
    }

    @Override
    public List<User> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional(readOnly=false)
    public User update(User user) {
        return dao.update(user);
    }

    @Override
    @Transactional(readOnly=false)
    public void delete(Long id) {
        dao.delete(id);
    }

    @Override
    public User findById(Long id) {
        return dao.findById(id);
    }
}
