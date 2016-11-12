package me.gking2224.securityms.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gking2224.securityms.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Role findByName(String role);

}
