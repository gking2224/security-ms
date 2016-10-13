package me.gking2224.securityms.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gking2224.securityms.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

    User findByUsername(String username);

}
