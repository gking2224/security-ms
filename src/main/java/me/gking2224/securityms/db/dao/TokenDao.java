package me.gking2224.securityms.db.dao;

import java.util.List;

import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.securityms.model.Token;

public interface TokenDao extends CrudDao<Token, Long>{

    List<String> deleteExpired();

    List<String> invalidateUserSession(String username, String comment);

}
