package me.gking2224.securityms.db.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gking2224.securityms.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long>{

    List<Token> findByExpiryLessThan(Long cutoff);

    List<Token> findByUserUsername(String username);
}
