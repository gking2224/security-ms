package me.gking2224.securityms.db.dao;

import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.securityms.model.User;

public interface UserDao extends CrudDao<User, Long>{

    User findByUsername(String username);

}
