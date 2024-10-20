package com.example.BE_PBL6_FastOrderSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
@Data
@Entity
@NoArgsConstructor
public class Role {
    public enum name {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_OWNER,
        ROLE_SHIPPER,
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Collection<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public  String getName(){
        return name != null? name : "";
    }
}