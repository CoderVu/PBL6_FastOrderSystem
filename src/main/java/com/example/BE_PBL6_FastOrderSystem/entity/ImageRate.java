package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "image_rate")
public class ImageRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String imageId;
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    @ManyToOne
    @JoinColumn(name = "rate_id")
    private Rate rate;
}
