package me.gking2224.securityms.service;

import java.util.List;

import me.gking2224.securityms.model.User;

public interface ThingService {

    User create(User user);

    List<User> findAll();

    User update(User user);

    void delete(Long id);

    User findById(Long id);

}
