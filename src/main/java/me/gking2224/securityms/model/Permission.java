package me.gking2224.securityms.model;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import me.gking2224.common.model.NullAbstractEntity;
import me.gking2224.common.web.View.Summary;

@Entity
@Table
public class Permission extends NullAbstractEntity<Long> implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2707606975268392692L;

    private Long id;
    
    private String name;
    
    private Set<Permission> includes;
    
    private Permission parent;
    
    public Permission() {
        super();
    }
    
    public Permission(String name) {
        this.name = name;
    }

    public Permission(long id, String name) {
        this(name);
        this.id = id;
    }

    public Permission(final Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "permission_id")
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

    @OneToMany(fetch=FetchType.EAGER, mappedBy="parent")
    public Set<Permission> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<Permission> permissions) {
        this.includes = permissions;
    }

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="parent_id", nullable=true)
    public Permission getParent() {
        return parent;
    }

    public void setParent(Permission parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Permission other = (Permission) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Permission [id=%s, name=%s, includes=%s]", id, name, includes);
    }
}
