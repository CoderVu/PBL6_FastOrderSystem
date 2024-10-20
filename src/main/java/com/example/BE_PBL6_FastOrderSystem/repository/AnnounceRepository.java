package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.AnnounceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnounceRepository extends JpaRepository<AnnounceUser,Long> {
    List<AnnounceUser> findByUserid(Long userId);
}