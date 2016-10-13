package me.gking2224.securityms.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.web.View.Summary;
import me.gking2224.securityms.client.UserDetails;
import me.gking2224.securityms.service.PermissionsHelper;

@Entity
@Table
public class User implements java.io.Serializable {

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

    @Column(name="enabled", nullable = false, columnDefinition = "TINYINT", length = 1)
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
    public Set<Permission> getPermissions() {
        final Set<Permission> rv = new HashSet<Permission>();
        getRoles().stream().forEach(r->rv.addAll(r.getPermissions()));
        return rv;
    }

    @Transient
    public Set<String> getEffectivePermissions() {
        return PermissionsHelper.getEffectivePermissions(getPermissions());
    }
}
