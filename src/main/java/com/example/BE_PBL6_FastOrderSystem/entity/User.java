package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data
@Entity

@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "facebook_id"),
        @UniqueConstraint(columnNames = "sub")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "facebook_id")
    private String facebookId;
    @Column(name = "sub")
    private String sub;
    private String phoneNumber;
    private String password;
    private String fullName;
    @Column(name = "avatar", columnDefinition = "LONGTEXT")
    private String avatar;
    @Column(name = "email")
    private String email;
    private String address;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "is_approved")
    private Boolean isApproved = (Boolean) false;
    @Column(name = "is_active")
    private Boolean isActive = (Boolean) true;
    @Column(name = "is_busy")
    private Boolean isBusy = (Boolean) false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean accountLocked = (Boolean) false;
    private Integer rewardPoints;
    @OneToMany(mappedBy = "user")
    private List<UserVoucher> vouchers;
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