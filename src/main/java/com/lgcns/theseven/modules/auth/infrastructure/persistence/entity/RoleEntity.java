package com.lgcns.theseven.modules.auth.infrastructure.persistence.entity;

import com.lgcns.theseven.common.base.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@MappedSuperclass
public class RoleEntity extends BaseEntity  {

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private  String description;
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<UserEntity> getUsers() { return users; }
    public void setUsers(Set<UserEntity> users) { this.users = users; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}