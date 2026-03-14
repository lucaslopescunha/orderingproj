package com.sop.orderingproj.entities;

import com.sop.orderingproj.enumeration.RoleName;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ROLES")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role() {}

    public Role(Integer id, RoleName name) {
        this.id = id;
        this.name = name;
    }

    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
    
    public static class Builder {
        
        private Integer id;
        private RoleName name;
        
        public Builder id(Integer id) {
            this.id = id;
            return this;
        }
        
        public Builder name(RoleName roleName) {
            this.name = roleName;
            return this;
        }
        public Role build() {
            return new Role(this);
        }
    }

    
}
