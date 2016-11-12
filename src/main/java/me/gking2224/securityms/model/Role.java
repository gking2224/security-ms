package me.gking2224.securityms.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.model.NullAbstractEntity;
import me.gking2224.common.web.View.Summary;

@Entity
@Table
public class Role extends NullAbstractEntity<Long> implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2707606975268392692L;

    private Long id;
    
    private String name;
    
    private Set<RolePermission> rolePermissions;
    
    public Role() {
        super();
    }
    
    public Role(String name) {
        this.name = name;
    }

    public Role(long id, String name) {
        this(name);
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="role_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    @JsonView(Summary.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Role [id=%s, name=%s]", id, name);
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy="pk.role", fetch=FetchType.LAZY)
    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public void addPermission(Permission p) {
        RolePermission rp = new RolePermission();
        RolePermissionId pk = new RolePermissionId();
        pk.setPermission(p);
        pk.setRole(this);
        rp.setPk(pk);
        rp.setEnabled(true);
        getRolePermissions().add(rp);
    }
}
