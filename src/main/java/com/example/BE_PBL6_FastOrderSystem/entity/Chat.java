package com.example.BE_PBL6_FastOrderSystem.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    User sender;
    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    User receiver;
    String message;
    LocalDateTime localTime;
    @Column(nullable = false, columnDefinition = "boolean default false")
    Boolean isRead = false;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    @PrePersist
    protected void onCreate() {
        localTime = LocalDateTime.now();
    }
}
