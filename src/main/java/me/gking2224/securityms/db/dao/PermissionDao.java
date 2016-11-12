package me.gking2224.securityms.db.dao;

import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.securityms.model.Permission;

public interface PermissionDao extends CrudDao<Permission, Long>{

    Permission findExisting(String name);
}
