package com.example.BE_PBL6_FastOrderSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String facebookId;
    private String phoneNumber;
    private String password;
    private String fullName;
    @Column(name = "avatar", columnDefinition = "LONGTEXT")
    private String avatar;
    private String email;
    private String address;
    @Column(name = "longitude", columnDefinition = "DOUBLE")
    private Double longitude;
    @Column(name = "latitude", columnDefinition = "DOUBLE")
    private Double latitude;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_busy")
    private Boolean isBusy = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean accountLocked;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }
}