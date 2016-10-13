package me.gking2224.securityms.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gking2224.securityms.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>{

}
