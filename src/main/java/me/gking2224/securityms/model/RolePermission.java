package me.gking2224.securityms.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
@AssociationOverrides({
    @AssociationOverride(name = "pk.role", 
        joinColumns = @JoinColumn(name = "role_id")),
    @AssociationOverride(name = "pk.permission", 
        joinColumns = @JoinColumn(name = "permission_id")) })
public class RolePermission implements AssignedPermission {
    
    private RolePermissionId pk;
    
    private boolean enabled;


    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @EmbeddedId
    public RolePermissionId getPk() {
        return pk;
    }

    public void setPk(RolePermissionId pk) {
        this.pk = pk;
    }
    
    @Transient
    public Permission getPermission() {
        return getPk().getPermission();
    }
    
    @Transient
    public Role getRole() {
        return getPk().getRole();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((pk == null) ? 0 : pk.hashCode());
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
        RolePermission other = (RolePermission) obj;
        if (enabled != other.enabled)
            return false;
        if (pk == null) {
            if (other.pk != null)
                return false;
        } else if (!pk.equals(other.pk))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RolePermission [pk=%s, enabled=%s]", pk, enabled);
    }
}
