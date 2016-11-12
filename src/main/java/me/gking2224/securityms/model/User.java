package me.gking2224.securityms.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.model.NullAbstractEntity;
import me.gking2224.common.web.View.Summary;
import me.gking2224.securityms.client.UserDetails;
import me.gking2224.securityms.service.PermissionsHelper;

@Entity
@Table
public class User extends NullAbstractEntity<Long> implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2707606975268392692L;

    private Long id;
    
    private String username;
    
    private String firstName;
    
    private String surname;
    
    private String password;
    
    private boolean enabled = true;
    
    private Set<Role> roles;

    private Set<UserPermission> userPermissions;
    
    public User() {
        super();
    }
    

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    @JsonView(Summary.class)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column
    @JsonView(Summary.class)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column
    @JsonView(Summary.class)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Column
    public String getPassword() {
        return password;
    }
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="pk.user", fetch=FetchType.LAZY)
    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }
    
    public void setUserPermissions(Set<UserPermission> userPermissions) {
        this.userPermissions = userPermissions;
    }

    @Override
    public String toString() {
        return String.format("User [id=%s, username=%s]", id, username);
    }

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="UserRole", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="role_id")})
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    public UserDetails getUserDetails() {
        UserDetails rv = new UserDetails();
        rv.setUserId(id);
        rv.setFirstName(firstName);
        rv.setSurname(surname);
        rv.setUsername(username);
        return rv;
    }

    @Transient
    public Set<AssignedPermission> getPermissions() {
        final Set<AssignedPermission> rv = new HashSet<AssignedPermission>();
        getRoles().stream().forEach(r->rv.addAll(r.getRolePermissions()));
        rv.addAll(getUserPermissions());
        return rv;
    }

    @Transient
    public Set<String> getEffectivePermissions() {
        return PermissionsHelper.getEffectivePermissions(getPermissions());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public void addPermission(Permission p) {
        UserPermission up = new UserPermission();
        UserPermissionId pk = new UserPermissionId();
        pk.setPermission(p);
        pk.setUser(this);
        up.setPk(pk);
        up.setEnabled(true);
        getUserPermissions().add(up);
    }


    public void addRole(Role r) {
        getRoles().add(r);
    }
}
