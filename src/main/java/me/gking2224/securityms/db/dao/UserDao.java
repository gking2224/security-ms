package me.gking2224.securityms.db.dao;

import java.util.List;

import me.gking2224.securityms.model.User;

public interface UserDao {

    User create(User user);

    List<User> findAll();

    User update(User user);

    void delete(Long id);

    User findById(Long id);


}
