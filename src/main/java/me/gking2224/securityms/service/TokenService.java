package me.gking2224.securityms.service;

import org.springframework.security.core.token.Token;

public interface TokenService extends org.springframework.security.core.token.TokenService{

    TokenBuilder newToken();

    Token allocateToken(me.gking2224.securityms.model.Token<?> token);

    String tokenToString(me.gking2224.securityms.model.Token<?> token);

    me.gking2224.securityms.model.Token<?> tokenFromString(String k);

}
