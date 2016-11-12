package me.gking2224.securityms.db.dao;

import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.securityms.model.Role;

public interface RoleDao extends CrudDao<Role, Long>{

    Role findByName(String role);


}
