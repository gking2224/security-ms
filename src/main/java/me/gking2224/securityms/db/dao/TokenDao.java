package me.gking2224.securityms.db.dao;

import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.securityms.model.Token;

public interface TokenDao extends CrudDao<Token, Long>{

    int deleteExpired();

    int invalidateUserSession(String username, String comment);

}
