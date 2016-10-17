package me.gking2224.securityms.db.dao;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.AbstractDaoImpl;
import me.gking2224.common.utils.DurationFormatter;
import me.gking2224.securityms.db.jpa.TokenRepository;
import me.gking2224.securityms.model.Token;

@Component
@Transactional
public class TokenDaoImpl extends AbstractDaoImpl<Token, Long> implements TokenDao {

    @Autowired
    protected TokenRepository repository;
    
    public TokenDaoImpl() {
    }
    
    @Override
    protected JpaRepository<Token, Long> getRepository() {
        return repository;
    }

    @Override
    public int deleteExpired() {
        
        Long cutoff = Instant.now().minus(deleteExpiredGracePeriod()).toEpochMilli();
        List<Token> expired = repository.findByExpiryLessThan(cutoff);
        int rv = expired.size();
        repository.deleteInBatch(expired);
        return rv;
    }

    private Duration deleteExpiredGracePeriod() {
        return DurationFormatter.getInstance().apply("6m");
    }

    @Override
    public int invalidateUserSession(final String username, final String comment) {
        List<Token> tokens = repository.findByUserUsername(username);
        int rv = tokens.size();
        tokens.forEach(t -> {
           t.invalidate(comment); 
        });
        
        repository.save(tokens);
        return rv;
    }
}
