package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sizeId;
    private String name;
}